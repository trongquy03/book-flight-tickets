package com.kttt.webbanve.repositories;

import com.kttt.webbanve.models.OrderInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.ArrayList;

public interface OrderRepositories extends JpaRepository<OrderInfo,Integer> {
    public OrderInfo getOrderInfoByOrderID(int orderID);

    @Query(value = "select * from order_infor",nativeQuery = true)
    public ArrayList<OrderInfo> getAllOrderInfos();
    public OrderInfo getOrderInfoByQrCode(String qrCode);
}
