package com.kttt.webbanve.controllers.admin;

import com.kttt.webbanve.models.Flight;
import com.kttt.webbanve.repositories.FlightRepository;
import com.kttt.webbanve.services.FlightService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class FlightAdminController {
    private FlightService flightService;

    public FlightAdminController(FlightService flightService) {
        this.flightService = flightService;
    }

    @GetMapping("/admin/flightList")
    public String getFirstPage(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        if(session.getAttribute("role")==null){
            return "admin/loginAdmin";
        }
        return getAllFlights(model, request,1, 5, "flightID", "asc");
    }

    @GetMapping("/admin/flightList/page/{pageNo}")
    public String getAllFlights(Model model,
                                HttpServletRequest request,
                                @PathVariable(name = "pageNo") int pageNo,
                                @RequestParam(defaultValue = "5", required = false) int pageSize,
                                @RequestParam(defaultValue = "flightID", required = false) String sortField,
                                @RequestParam(defaultValue = "asc", required = false) String sortDir
    ) {
        try {
            HttpSession session = request.getSession();
            if(session.getAttribute("role")=="1" || session.getAttribute("role")==null){
                return "admin/loginAdmin";
            }
            Page<Flight> flights = flightService.findAll(pageNo, pageSize, sortField, sortDir);
            long startCount = (long) (pageNo - 1) * pageSize + 1;
            long endCount = startCount + pageSize -1;
            if (endCount > flights.getTotalElements()) {
                endCount = flights.getTotalElements();
            }
            String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

            model.addAttribute("reverseSortDir", reverseSortDir);
            model.addAttribute("flights", flights);
            model.addAttribute("currentPage", pageNo);
            model.addAttribute("totalPages", flights.getTotalPages());
            model.addAttribute("startCount", startCount);
            model.addAttribute("endCount", endCount);
            model.addAttribute("totalItems", flights.getTotalElements());
            model.addAttribute("sortField", sortField);
            model.addAttribute("sortDir", sortDir);
            model.addAttribute("keyword", null);
            return "admin/flight/FlightList";
        } catch (Exception e) {
            return "redirect:/admin/flightList";
        }
    }

    @GetMapping("admin/addFlight")
    public String getAddFlight(HttpServletRequest request) {
        HttpSession session = request.getSession();
        if(session.getAttribute("role")=="1" || session.getAttribute("role")==null){
            return "admin/loginAdmin";
        }
        return "admin/flight/AddFlight";
    }

    @PostMapping("admin/addFlight")
    public String addFlight(@ModelAttribute("flight") Flight flight,
                            HttpServletRequest request,
                            RedirectAttributes redirectAttributes) {
        String feeFlightx = request.getParameter("feeFlightx");
        try {
            if(flight.getDepartingFrom().trim().equals("")){
                redirectAttributes.addFlashAttribute("departingForm", "Điểm khởi hành không được để trống!");
                return "redirect:/admin/addFlight";
            }
            if(flight.getArrivingAt().trim().equals("")) {
                redirectAttributes.addFlashAttribute("arrivingAt", "Điểm đến không được để trống!");
                return "redirect:/admin/addFlight";
            }
            if(flight.getFlightTime().trim().equals("")) {
                redirectAttributes.addFlashAttribute("flightTime", "Giờ bay không được để trống!");
                return "redirect:/admin/addFlight";
            } else if (Integer.parseInt(flight.getFlightTime())<1) {
                redirectAttributes.addFlashAttribute("flightTime", "Giờ bay phải lớn hơn 0!");
                return "redirect:/admin/addFlight";
            }
            if(flight.getDepartureTime().trim().equals("")) {
                redirectAttributes.addFlashAttribute("departureTime", "Giờ khởi hành không được để trống!");
                return "redirect:/admin/addFlight";
            } else if (Integer.parseInt(flight.getDepartureTime())<1) {
                redirectAttributes.addFlashAttribute("departureTime", "Giờ khởi hành phải lớn hơn 0!");
                return "redirect:/admin/addFlight";
            }
            if(feeFlightx.equals("")) {
                redirectAttributes.addFlashAttribute("feeFlight", "Phí chuyến bay không được để trống!");
                return "redirect:/admin/addFlight";
            } else if(Integer.parseInt(feeFlightx)<=0) {
                redirectAttributes.addFlashAttribute("feeFlight", "Phí chuyến bay phải lớn hơn 0!");
                return "redirect:/admin/addFlight";
            }
            flight.setFeeFlight(Long.parseLong(feeFlightx));
            flightService.save(flight);
            return "redirect:/admin/flightList";
        } catch (Exception e) {
            return "admin/flight/AddFlight";
        }
    }

    @GetMapping("admin/updateFlight/{flightId}")
    public String updateFlight(@PathVariable Integer flightId,
                               ModelMap modelMap,
                               HttpServletRequest request) {
        HttpSession session = request.getSession();
        if(session.getAttribute("role")=="1" || session.getAttribute("role")==null){
            return "admin/loginAdmin";
        }
        modelMap.addAttribute("flight", flightService.findById(flightId).get());
        return "admin/flight/UpdateFlight";
    }

    @PostMapping("admin/updateFlight/{flightId}")
    public String updateFlight(@ModelAttribute("flight") Flight flight,
                               ModelMap modelMap,
                               HttpServletRequest request,
                               RedirectAttributes redirectAttributes,
                               @PathVariable Integer flightId
    ){
        String feeFlightx = request.getParameter("feeFlightx");
        try {
            if(flightService.findById(flightId).isPresent()) {
                Flight foundFlight = flightService.findById(flightId).get();
                if(flight.getDepartingFrom().trim().equals("")){
                    redirectAttributes.addFlashAttribute("departingForm", "Điểm khởi hành không được để trống!");
                    return "redirect:/admin/updateFlight/{flightId}";
                }

                if(flight.getArrivingAt().trim().equals("")) {
                    redirectAttributes.addFlashAttribute("arrivingAt", "Điểm đến không được để trống!");
                    return "redirect:/admin/updateFlight/{flightId}";
                }

                if(flight.getFlightTime().trim().equals("")) {
                    redirectAttributes.addFlashAttribute("flightTime", "Giờ bay không được để trống!");
                    return "redirect:/admin/updateFlight/{flightId}";
                } else if (Integer.parseInt(flight.getFlightTime())<1) {
                    redirectAttributes.addFlashAttribute("flightTime", "Giờ bay phải lớn hơn 0!");
                    return "redirect:/admin/updateFlight/{flightId}";
                }

                if(flight.getDepartureTime().trim().equals("")) {
                    redirectAttributes.addFlashAttribute("departureTime", "Giờ khởi hành không được để trống!");
                    return "redirect:/admin/updateFlight/{flightId}";
                } else if (Integer.parseInt(flight.getDepartureTime())<1) {
                    redirectAttributes.addFlashAttribute("departureTime", "Giờ khởi hành phải lớn hơn 0!");
                    return "redirect:/admin/updateFlight/{flightId}";
                }

                if(feeFlightx.equals("")) {
                    redirectAttributes.addFlashAttribute("feeFlight", "Phí chuyến bay không được để trống!");
                    return "redirect:/admin/updateFlight/{flightId}";
                } else if(Integer.parseInt(feeFlightx)<=0) {
                    redirectAttributes.addFlashAttribute("feeFlight", "Phí chuyến bay phải lớn hơn 0!");
                    return "redirect:/admin/updateFlight/{flightId}";
                }
                foundFlight.setDepartingFrom(flight.getDepartingFrom());
                foundFlight.setArrivingAt(flight.getArrivingAt());
                foundFlight.setFlightTime(flight.getFlightTime());
                foundFlight.setDepartureTime(flight.getDepartureTime());
                foundFlight.setFeeFlight(Long.parseLong(feeFlightx));
                flightService.save(foundFlight);
            }
            return "redirect:/admin/flightList";
        } catch (Exception e) {
            System.out.println(e);
            return "redirect:/admin/updateFlight/{flightId}";
        }
    }

    @PostMapping("admin/deleteFlight/{flightId}")
    public String deleteFlight(@PathVariable int flightId) {
        flightService.deleteById(flightId);
        return "redirect:/admin/flightList";
    }

    @GetMapping("admin/flight/search")
    public String searchFlight(@RequestParam(defaultValue = "", required = false) String search, ModelMap modelMap) {
        List<Flight> flights = flightService.searchFlights(search);
        modelMap.addAttribute("flights", flights);
        modelMap.addAttribute("query", search);
        return "admin/flight/SearchFlight";
    }
}
