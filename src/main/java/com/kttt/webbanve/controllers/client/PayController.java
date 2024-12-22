package com.kttt.webbanve.controllers.client;

import com.kttt.webbanve.models.Customer;
import com.kttt.webbanve.models.OrderInfo;
import com.kttt.webbanve.models.Ticket;
import com.kttt.webbanve.models.supportModels.FlightSelected;
import com.kttt.webbanve.models.supportModels.PayForm;
import com.kttt.webbanve.onlinePay.config.Config;
import com.kttt.webbanve.repositories.*;
import com.kttt.webbanve.security.configuration.Barcode;
import com.kttt.webbanve.services.*;
import com.kttt.webbanve.services.GeneratePdfService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/payment")
public class PayController {

    @Autowired
    FlightService flightService;
    @Autowired
    SeatCategoryRepositories sr;
    @Autowired
    SeatRepositories seatRepositoriesr;
    @Autowired
    LuggageRepositories lr;
    @Autowired
    FlightRepositories fr;
    @Autowired
    PlaneRepositories pr;
    @Autowired
    CustomerRepositories cr;
    @Autowired
    CustomerServiceImpl customerService;
    @Autowired
    OrderRepositories or;
    @Autowired
    TicketRepositories tr;
    @Autowired
    TicketService ticketService;
    @Autowired
    MailSenderService mailSenderService;
    @Autowired
    GeneratePdfService generatePdfService;
    @Autowired
    private OrderInfoService orderInfoService;

    @PostMapping(path = "/create-payment")
    public String createPayment(HttpServletRequest request,PayForm cus, Model model) throws UnsupportedEncodingException {
        Customer customer = new Customer();
        Barcode makeBarcode = new Barcode();
        if(cus.getFullname() == null || cus.getPhone() == null || cus.getAddress() == null || cus.getEmail() == null || cus.getCitizenIdentification() == null){
            model.addAttribute("pageTitle","Thanh toán");
            model.addAttribute("error","Không được để trống!");
            return "client/payment";
        }
        String fullname = cus.getFullname();
        String email = cus.getEmail();
        String phone = cus.getPhone();
        String address = cus.getAddress();
        String citizen_identification = cus.getCitizenIdentification();
            if(request.getSession().getAttribute("customer") != null)
                customer = (Customer) request.getSession().getAttribute("customer");
            customer.setFullname(fullname);
            customer.setEmail(email);
            customer.setPhone(phone);
            customer.setAddress(address);
            customer.setCitizenIdentification(citizen_identification);
            Customer cus_after = cr.save(customer);
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            Date dateNow = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
            OrderInfo order = new OrderInfo();
            order.setCustomer(cus_after);
            order.setStatus(0);
            order.setTotal_cost((long)request.getSession().getAttribute("totalBill"));
            order.setDate(dateNow);
            String qrCode = "HD" + phone.substring(phone.length()-4) + citizen_identification.substring(citizen_identification.length()-4) + Config.getRandomNumber(4);
            order.setQrCode(qrCode);
            OrderInfo order_after = or.save(order);
            ArrayList<FlightSelected> flightSelecteds = (ArrayList<FlightSelected>) request.getSession().getAttribute("flightSelected");
            ArrayList<Ticket> tickets = new ArrayList<>();
            for(FlightSelected f : flightSelecteds){
                Ticket ticket = new Ticket();
                ticket.setAirfares(f.getAirfares());
                ticket.setFlight(f.getFlight());
                ticket.setCustomer(f.getCustomer());
                ticket.setSeat(f.getSeat());
                ticket.setOrder(order_after);
                ticket.setLuggage(f.getLuggage());
                String barCodeTicket = "T" + f.getCustomer().getPhone().substring(f.getCustomer().getPhone().length()-4) + f.getCustomer().getEmail().substring(0,4) + Config.getRandomNumber(4);
                ticket.setBarCode(barCodeTicket.toUpperCase());
                Ticket ticket_after = tr.save(ticket);
                tickets.add(ticket_after);
                makeBarcode.create_bar_code(barCodeTicket.toUpperCase());
            }
            makeBarcode.create_qr_code(Base64.getEncoder().encodeToString(order_after.getQrCode().getBytes()));
            request.getSession().setAttribute("tickets",tickets);

//======================================================================================================================
            long amount = (long)request.getSession().getAttribute("totalBill")*100;

            String vnp_TxnRef = order_after.getQrCode();
            String vnp_IpAddr = Config.getIpAddress(request);

            Map<String, String> vnp_Params = new HashMap<>();
            vnp_Params.put("vnp_Version", Config.vnp_Version);
            vnp_Params.put("vnp_Command", Config.vnp_Command);
            vnp_Params.put("vnp_TmnCode", Config.vnp_TmnCode);
            vnp_Params.put("vnp_Amount", String.valueOf(amount));
            vnp_Params.put("vnp_CurrCode", "VND");
            vnp_Params.put("vnp_Locale", "vn");
            vnp_Params.put("vnp_BankCode", "");
            vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
            vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + vnp_TxnRef);
            vnp_Params.put("vnp_OrderType", Config.orderType);
            vnp_Params.put("vnp_ReturnUrl", Config.vnp_ReturnUrl);
            vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

            Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String vnp_CreateDate = formatter.format(cld.getTime());
            vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

            cld.add(Calendar.MINUTE, 15);
            String vnp_ExpireDate = formatter.format(cld.getTime());
            vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

            List fieldNames = new ArrayList(vnp_Params.keySet());
            Collections.sort(fieldNames);
            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();
            Iterator itr = fieldNames.iterator();
            while (itr.hasNext()) {
                String fieldName = (String) itr.next();
                String fieldValue = (String) vnp_Params.get(fieldName);
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    //Build hash data
                    hashData.append(fieldName);
                    hashData.append('=');
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    //Build query
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    if (itr.hasNext()) {
                        query.append('&');
                        hashData.append('&');
                    }
                }
            }
            String queryUrl = query.toString();
            String vnp_SecureHash = Config.hmacSHA512(Config.secretKey, hashData.toString());
            queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
            String paymentUrl = Config.vnp_PayUrl + "?" + queryUrl;
            return paymentUrl;
    }

    @PostMapping("/ticket/export")
    public OrderInfo export(String qrcodeEncoded){
        byte[] qrcode = Base64.getDecoder().decode(qrcodeEncoded);
        OrderInfo orderInfo = orderInfoService.getOrderByQrcode(new String(qrcode));
        if(orderInfo == null)
            return null;
        return orderInfo;
    }
    @PostMapping("/ticket/qrreader/success")
    public String readerSuccess(@RequestBody OrderInfo orderInfo) throws IOException, MessagingException {
        if(orderInfo == null||orderInfo.getStatus()>0)
            return "/admin/ticket/qrreader/fail";
        ArrayList<Ticket> tickets = ticketService.getTicketsByOrderID(orderInfo.getOrderID());
        generatePdfService.exportTicket(tickets);
        String body = "Xin chào "+orderInfo.getCustomer().getFullname()+"\n\tVé máy bay của bạn đã được tạo thành công!\n\t Tệp đính kèm phía dưới.";
        String subject = "GOGO - EXPORT PDF TICKET";
        mailSenderService.sendMailWithAttachment_Ticket(orderInfo.getCustomer().getEmail(),body,subject,tickets);
        return "/admin/ticket/qrreader/success";
    }

    @PostMapping("/searchOrder")
    public OrderInfo searchOrder(String qrcodeEncoded){
        byte[] qrcode = Base64.getDecoder().decode(qrcodeEncoded);
        OrderInfo orderInfo = orderInfoService.getOrderByQrcode(new String(qrcode));
        if(orderInfo == null)
            return null;
        return orderInfo;
    }
    @PostMapping("/showOrder")
    public String showOrder(@RequestBody OrderInfo orderInfo,HttpServletRequest request){
        ArrayList<Ticket> tickets = ticketService.getTicketsByOrderID(orderInfo.getOrderID());
        request.getSession().setAttribute("tickets",tickets);
        request.getSession().setAttribute("order",orderInfo);
        return "/flights/searchOrder";
    }
}
