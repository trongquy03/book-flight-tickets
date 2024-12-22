package com.kttt.webbanve.controllers;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.HttpServerErrorException;

@Controller
public class ExceptionController implements ErrorController {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model){
        String pageTitle="";
        String errorPage="";
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if(status != null){
            Integer statusCode = Integer.valueOf(status.toString());
            if(statusCode == HttpStatus.NOT_FOUND.value()){
                pageTitle = "Không tìm thấy trang";
                errorPage = "error/404";
                log.error("Error 404");
//                Exception exception = (Exception) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
//                log.error(exception.getMessage(),exception);
            } else if(statusCode == HttpStatus.FORBIDDEN.value()){
                pageTitle = "Lỗi hệ thống";
                errorPage = "error/403";
                log.error("Error 403");
                Exception exception = (Exception) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
                log.error(exception.getMessage(),exception);
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                pageTitle = "Lỗi hệ thống";
                errorPage = "error/500";
                log.error("Error 500");
                Exception exception = (Exception) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
                exception.printStackTrace();
            }
            else {
                pageTitle = "Lỗi hệ thống";
                errorPage = "error/500";
                log.error("Error 500");
                Exception exception = (Exception) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
                if(exception == null)
                    log.info("Không tìm thấy ngoại lệ.");
                else
                    log.error(exception.getMessage(),exception);
            }
        }
        model.addAttribute("pageTitle",pageTitle);
        return errorPage;
    }

}
