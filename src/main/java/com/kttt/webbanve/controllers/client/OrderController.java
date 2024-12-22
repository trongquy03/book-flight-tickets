package com.kttt.webbanve.controllers.client;
import com.kttt.webbanve.services.OrderInfoService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;


@Controller
public class OrderController {
    @Autowired
    OrderInfoService orderInfoService;
    final Logger logger = LoggerFactory.getLogger(this.getClass());
    @PostMapping("/api/order/refund")
    public String refund(HttpServletRequest req, Model model){
        try{
            String response = orderInfoService.refund(req);
            model.addAttribute("resp",response);
            return "client/vnpay_refund";
        }catch (IOException e){
            logger.error(e.getMessage());
            model.addAttribute("error",e.getMessage());
            return "client/vnpay_refund";
        }
    }

    @GetMapping("/api/order/form_query")
    public String form_query(){
        return "client/vnpay_query";
    }
    @PostMapping("/api/order/query")
    public String query(HttpServletRequest req,Model model) throws IOException {
        String response = orderInfoService.queryOrder(req);
        model.addAttribute("resp",response);
        return "client/vnpay_query";
    }
}
