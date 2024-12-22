package com.kttt.webbanve.services;

import com.kttt.webbanve.models.OrderInfo;
import com.kttt.webbanve.models.Ticket;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
public class DataMapper {
    public Context setData(OrderInfo order, ArrayList<Ticket> tickets){
        Context context = new Context();
        Map<String, Object> data = new HashMap<>();
        data.put("order",order);
        data.put("tickets",tickets);
        context.setVariables(data);
        return context;
    }
}
