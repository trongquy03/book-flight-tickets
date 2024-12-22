package com.kttt.webbanve.controllers.admin;

import com.kttt.webbanve.models.OrderInfo;
import com.kttt.webbanve.models.Ticket;
import com.kttt.webbanve.services.OrderInfoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

@Controller
public class OrderInfoController {
    @Autowired
    OrderInfoService orderInfoService;
    @GetMapping("/admin/orderList")
    public String getFirstPage(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        if(session.getAttribute("role")==null){
            return "admin/loginAdmin";
        }
        return getAllOrder(model, request,1, 5, "orderID", "asc");
    }
    @GetMapping("/admin/orderList/page/{pageNo}")
    public String getAllOrder(Model model,
                               HttpServletRequest request,
                               @PathVariable(name = "pageNo") int pageNo,
                               @RequestParam(defaultValue = "5", required = false) int pageSize,
                               @RequestParam(defaultValue = "orderID", required = false) String sortField,
                               @RequestParam(defaultValue = "asc", required = false) String sortDir) {
        try {
            HttpSession session = request.getSession();
            if(session.getAttribute("role")=="1" || session.getAttribute("role")==null){
                return "admin/loginAdmin";
            }

            Page<OrderInfo> orderInfos = orderInfoService.findAll(pageNo, pageSize, sortField, sortDir);

            long startCount = (long) (pageNo - 1) * pageSize + 1;
            long endCount = startCount + pageSize -1;
            if (endCount > orderInfos.getTotalElements()) {
                endCount = orderInfos.getTotalElements();
            }
            String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

            model.addAttribute("reverseSortDir", reverseSortDir);
            model.addAttribute("orders", orderInfos);
            model.addAttribute("currentPage", pageNo);
            model.addAttribute("totalPages", orderInfos.getTotalPages());
            model.addAttribute("startCount", startCount);
            model.addAttribute("endCount", endCount);
            model.addAttribute("totalItems", orderInfos.getTotalElements());
            model.addAttribute("sortField", sortField);
            model.addAttribute("sortDir", sortDir);
            model.addAttribute("keyword", null);
            return "admin/order/orderList";
        } catch (Exception e) {
            System.out.println(e);
            return "redirect:/admin/orderList";
        }
    }

    @PostMapping("/admin/cancel")
    public String confirmCancel(HttpServletRequest request){
        OrderInfo orderInfo = orderInfoService.getOrderByID(Integer.parseInt(request.getParameter("orderID")));
        orderInfo.setStatus(2);
        orderInfoService.saveOrder(orderInfo);
        return "redirect:/admin/orderList";
    }

}
