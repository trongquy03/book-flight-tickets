package com.kttt.webbanve.services;

import com.kttt.webbanve.models.OrderInfo;
import com.kttt.webbanve.payload.CostStatistics;
import com.kttt.webbanve.payload.CostStatisticsByQuarter;
import com.kttt.webbanve.payload.OrderInfoDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public interface OrderInfoService {
    Page<OrderInfo> findAll(int pageNo, int pageSize, String sortBy, String sortDir);
    OrderInfo getOrderByQrcode(String Qrcode);
    OrderInfo getOrderByID(int orderID);
    void saveOrder(OrderInfo orderInfo);
    ArrayList<OrderInfo> getAllOrders();
//    List<OrderInfoDto> findAll();
    List<CostStatistics> statisticsCostByMonth();
    List<CostStatisticsByQuarter> costStatisticsByQuarter();
    void updateOrderStatus();

    String refund(HttpServletRequest req) throws IOException;

    String queryOrder(HttpServletRequest req) throws IOException;
}
