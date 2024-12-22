package com.kttt.webbanve.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kttt.webbanve.models.OrderInfo;
import com.kttt.webbanve.onlinePay.config.Config;
import com.kttt.webbanve.payload.CostStatistics;
import com.kttt.webbanve.payload.CostStatisticsByQuarter;
import com.kttt.webbanve.repositories.OrderRepositories;
import com.kttt.webbanve.repositories.StatisticsRepoCustomImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;


@Service
public class OrderInfoServiceImpl implements OrderInfoService{
    public final Logger logger = LoggerFactory.getLogger(this.getClass());
    private OrderRepositories orderRepositories;
    private StatisticsRepoCustomImpl srci;

    @Autowired
    public OrderInfoServiceImpl(StatisticsRepoCustomImpl srci, OrderRepositories orderRepositories) {
        this.orderRepositories = orderRepositories;
        this.srci = srci;
    }

//    @Override
//    public List<OrderInfoDto> findAll() {
//        List<OrderInfo> orderInfos = orderInfoRepository.findAll();
//        return orderInfos.stream().map(this::mapToDto).toList();
//    }

    @Override
    public Page<OrderInfo> findAll(int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        return orderRepositories.findAll(pageable);
    }

    @Override
    public OrderInfo getOrderByQrcode(String Qrcode) {
        return orderRepositories.getOrderInfoByQrCode(Qrcode);
    }

    @Override
    public OrderInfo getOrderByID(int orderID) {
        return orderRepositories.getOrderInfoByOrderID(orderID);
    }

    @Override
    public void saveOrder(OrderInfo orderInfo) {
        orderRepositories.save(orderInfo);
    }

    @Override
    public ArrayList<OrderInfo> getAllOrders() {
        return orderRepositories.getAllOrderInfos();
    }

    @Override
    public List<CostStatistics> statisticsCostByMonth() {
        return srci.statisticCostByMonth();
    }

    @Override
    public List<CostStatisticsByQuarter> costStatisticsByQuarter() {
        return srci.costStatisticsByQuarter();
    }

    @Override
    @Scheduled(fixedDelay = 5000)
    public void updateOrderStatus() {   //Tự động hủy hóa đơn sau 72 giờ
        Calendar calendar = Calendar.getInstance();
        ArrayList<OrderInfo> orders = getAllOrders();
        for(OrderInfo orderInfo : orders){
            Date now = calendar.getTime();
            calendar.setTime(orderInfo.getDate());
            calendar.add(Calendar.HOUR_OF_DAY,72);
            Date dateToCancel =  calendar.getTime();
            if(now.compareTo(dateToCancel) >= 0 && orderInfo.getStatus()==1){
                orderInfo.setStatus(2);
                saveOrder(orderInfo);
            }
        }
    }

    @Transactional
    @Override
    public String refund(HttpServletRequest req) throws IOException {
        String vnp_RequestId = Config.getRandomNumber(8);
        String vnp_Version = "2.1.0";
        String vnp_Command = "refund";
        String vnp_TmnCode = Config.vnp_TmnCode;
        String vnp_TransactionType = req.getParameter("trantype");
        String vnp_TxnRef = req.getParameter("order_id");
        long amount = Integer.parseInt(req.getParameter("amount"))*100;
        String vnp_Amount = String.valueOf(amount);
        String vnp_OrderInfo = "Hoan tien GD OrderId:" + vnp_TxnRef;
        String vnp_TransactionNo = ""; //Assuming value of the parameter "vnp_TransactionNo" does not exist on your system.
        String vnp_TransactionDate = req.getParameter("trans_date");
        String vnp_CreateBy = req.getParameter("user");

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());

        String vnp_IpAddr = Config.getIpAddress(req);

        Map<String, String> vnp_Params = new HashMap<>();

        vnp_Params.put("vnp_RequestId", vnp_RequestId);
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_TransactionType", vnp_TransactionType);
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_Amount", vnp_Amount);
        vnp_Params.put("vnp_OrderInfo", vnp_OrderInfo);

        if(vnp_TransactionNo != null && !vnp_TransactionNo.isEmpty())
        {
            vnp_Params.put("vnp_TransactionNo", "{get value of vnp_TransactionNo}");
        }
        if(req.getSession().getAttribute("transNo") != null)
            vnp_Params.put("vnp_TransactionNo",req.getSession().getAttribute("transNo").toString());
        vnp_Params.put("vnp_TransactionDate", vnp_TransactionDate);
        vnp_Params.put("vnp_CreateBy", vnp_CreateBy);
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        String hash_Data= String.join("|", vnp_RequestId, vnp_Version, vnp_Command, vnp_TmnCode,
                vnp_TransactionType, vnp_TxnRef, vnp_Amount, vnp_TransactionNo, vnp_TransactionDate,
                vnp_CreateBy, vnp_CreateDate, vnp_IpAddr, vnp_OrderInfo);

        String vnp_SecureHash = Config.hmacSHA512(Config.secretKey, hash_Data.toString());

        vnp_Params.put("vnp_SecureHash", vnp_SecureHash);

        URL url = new URL (Config.vnp_ApiUrl);
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
//        wr.writeBytes(vnp_Params.toString());
        wr.writeBytes(new ObjectMapper().writeValueAsString(vnp_Params));
        wr.flush();
        wr.close();
        int responseCode = con.getResponseCode();
        logger.info("nSending 'POST' request to URL : " + url);
        logger.info("Post Data : " + new ObjectMapper().writeValueAsString(vnp_Params));
        logger.info("Response Code : " + responseCode);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String output;
        StringBuilder response = new StringBuilder();
        while ((output = in.readLine()) != null) {
            response.append(output);
        }
        in.close();
        return response.toString();
    }

    @Override
    @Transactional
    public String queryOrder(HttpServletRequest req) throws IOException {
        //Command:querydr

        String vnp_RequestId = Config.getRandomNumber(8);
        String vnp_Version = "2.1.0";
        String vnp_Command = "querydr";
        String vnp_TmnCode = Config.vnp_TmnCode;
        String vnp_TxnRef = req.getParameter("order_id");
        String vnp_OrderInfo = "Kiem tra ket qua GD OrderId:" + vnp_TxnRef;
        //String vnp_TransactionNo = req.getParameter("transactionNo");
        String vnp_TransDate = req.getParameter("trans_date");

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());

        String vnp_IpAddr = Config.getIpAddress(req);

        HashMap<String,String>  vnp_Params = new HashMap<>();

        vnp_Params.put("vnp_RequestId", vnp_RequestId);
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", vnp_OrderInfo);
        //vnp_Params.put("vnp_TransactionNo", vnp_TransactionNo);
        vnp_Params.put("vnp_TransactionDate", vnp_TransDate);
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        String hash_Data= String.join("|", vnp_RequestId, vnp_Version, vnp_Command, vnp_TmnCode, vnp_TxnRef, vnp_TransDate, vnp_CreateDate, vnp_IpAddr, vnp_OrderInfo);
        String vnp_SecureHash = Config.hmacSHA512(Config.secretKey, hash_Data.toString());

        vnp_Params.put("vnp_SecureHash", vnp_SecureHash);

        URL url = new URL (Config.vnp_ApiUrl);
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(new ObjectMapper().writeValueAsString(vnp_Params));
        wr.flush();
        wr.close();
        int responseCode = con.getResponseCode();
        logger.info("nSending 'POST' request to URL : " + url);
        logger.info("Post Data : " + vnp_Params);
        logger.info("Response Code : " + responseCode);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String output;
        StringBuilder response = new StringBuilder();
        while ((output = in.readLine()) != null) {
            response.append(output);
        }
        in.close();
        return response.toString();
    }
}
