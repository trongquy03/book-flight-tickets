package com.kttt.webbanve.controllers.client;

import com.kttt.webbanve.repositories.SeatCategoryRepositories;
import com.kttt.webbanve.repositories.SeatCategoryRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

@Controller
public class HomeController {
    @Autowired
    SeatCategoryRepositories seatCategoryRepositories;
    @GetMapping("/")
    public String index(Model model, HttpServletRequest req){
        model.addAttribute("pageTitle","Trang chủ");
        req.getSession().setAttribute("seatCategories", seatCategoryRepositories.findAll());
        return "client/index";
    }
}
