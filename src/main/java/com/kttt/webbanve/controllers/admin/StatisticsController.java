package com.kttt.webbanve.controllers.admin;

import com.kttt.webbanve.payload.*;
import com.kttt.webbanve.services.OrderInfoService;
import com.kttt.webbanve.services.TicketService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;

@Controller
@RequestMapping("/admin/statistics")
public class StatisticsController {
    private TicketService ticketService;
    private OrderInfoService orderInfoService;

    public StatisticsController(TicketService ticketService, OrderInfoService orderInfoService) {
        this.ticketService = ticketService;
        this.orderInfoService = orderInfoService;
    }

    @GetMapping("/ticketByMonth")
    public String getStatisticsTicketByMonth(ModelMap modelMap,
                                             @RequestParam(defaultValue = "2019") int year,
                                             @RequestParam(defaultValue = "5") int numberOfYear,
                                             HttpServletRequest request) {
        HttpSession session = request.getSession();
        if(session.getAttribute("role")==null){
            return "admin/loginAdmin";
        }

        List<TicketStatistics> ticketDataList = ticketService.countTicket();
        List<Integer> uniqueYears = ticketService.getUniqueYear();
        List<Integer> getNumberYearsFrom = ticketService.getNumberYearsFrom(year, numberOfYear);

        modelMap.addAttribute("ticketDataList", ticketDataList);
        modelMap.addAttribute("uniqueYears", uniqueYears);
        modelMap.addAttribute("getNumberYearsFrom", getNumberYearsFrom);
        modelMap.addAttribute("numberOfYear", numberOfYear);
        modelMap.addAttribute("year",year);
        return "admin/statistics/ticketByMonth";
    }

    @GetMapping("/ticketByQuarter")
    public String getStatisticsTicketByQuarter(ModelMap modelMap,
                                               HttpServletRequest request) {
        HttpSession session = request.getSession();
        if(session.getAttribute("role")=="1" || session.getAttribute("role")==null){
            return "admin/loginAdmin";
        }
        List<TicketStatisticsByQuarter> ticketStatisticsByQuarters = ticketService.countTicketByQuarter();
        modelMap.addAttribute("ticketDataList", ticketStatisticsByQuarters);
        return "admin/statistics/ticketByQuarter";
    }

    @GetMapping("/ticketPerOrder")
    public String gerStatisticsTicketPerOrder(ModelMap modelMap,
                                              HttpServletRequest request) {
        HttpSession session = request.getSession();
        if(session.getAttribute("role")=="1" || session.getAttribute("role")==null){
            return "admin/loginAdmin";
        }
        List<CountOrderOfQuantityTicket> countOrderOfQuantityTicket = ticketService.ticketPerOrder();
        modelMap.addAttribute("countOrderOfQuantityTicket", countOrderOfQuantityTicket);
        return "admin/statistics/ticketPerOder";
    }

    @GetMapping("/incomeByMonth")
    public String getStatisticsCostByMonth(ModelMap modelMap,
                                             @RequestParam(defaultValue = "2019") int year,
                                             @RequestParam(defaultValue = "5") int numberOfYear,
                                             HttpServletRequest request) {
        HttpSession session = request.getSession();
        if(session.getAttribute("role")=="1" || session.getAttribute("role")==null){
            return "admin/loginAdmin";
        }

        List<CostStatistics> costDataList = orderInfoService.statisticsCostByMonth();
        List<Integer> uniqueYears = ticketService.getUniqueYear();
        List<Integer> getNumberYearsFrom = ticketService.getNumberYearsFrom(year, numberOfYear);

        modelMap.addAttribute("costDataList", costDataList);
        modelMap.addAttribute("uniqueYears", uniqueYears);
        modelMap.addAttribute("getNumberYearsFrom", getNumberYearsFrom);
        modelMap.addAttribute("numberOfYear", numberOfYear);
        modelMap.addAttribute("year",year);
        return "admin/statistics/totalCostByMonth";
    }

    @GetMapping("/incomeByQuarter")
    public String getStatisticsCostByQuarter(ModelMap modelMap,
                                               HttpServletRequest request) {
        HttpSession session = request.getSession();
        if(session.getAttribute("role")=="1" || session.getAttribute("role")==null){
            return "admin/loginAdmin";
        }

        List<CostStatisticsByQuarter> costStatisticsByQuarters = orderInfoService.costStatisticsByQuarter();
        modelMap.addAttribute("costDataList", costStatisticsByQuarters);
        return "admin/statistics/totalCostByQuarter";
    }
}
