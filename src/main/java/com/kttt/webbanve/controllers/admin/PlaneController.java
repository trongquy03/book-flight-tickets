package com.kttt.webbanve.controllers.admin;

import com.kttt.webbanve.AbstractClass;
import com.kttt.webbanve.models.AirlineCompany;
import com.kttt.webbanve.models.Plane;
import com.kttt.webbanve.repositories.AirlineCompanyRepository;
import com.kttt.webbanve.repositories.PlaneRepository;
import com.kttt.webbanve.services.AirlineCompanyService;
import com.kttt.webbanve.services.PlaneService;
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
public class PlaneController extends AbstractClass {
    private PlaneService planeService;
    private AirlineCompanyService airlineCompanyService;

    public PlaneController(PlaneService planeService, AirlineCompanyService airlineCompanyService) {
        this.planeService = planeService;
        this.airlineCompanyService = airlineCompanyService;
    }

    @GetMapping("/admin/listPlane")
    public String getFirstPage(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        if(session.getAttribute("role")==null || (int)session.getAttribute("role") == 0){
            return "admin/loginAdmin";
        }
        return getAllPlane(model, request,1, 5, "planeID", "asc");
    }

    @GetMapping("/admin/listPlane/page/{pageNo}")
    public String getAllPlane(Model model,
                              HttpServletRequest request,
                              @PathVariable(name = "pageNo") int pageNo,
                              @RequestParam(defaultValue = "5", required = false) int pageSize,
                              @RequestParam(defaultValue = "planeID", required = false) String sortField,
                              @RequestParam(defaultValue = "ASC", required = false) String sortDir
    ) {
        HttpSession session = request.getSession();
        if(session.getAttribute("role")==null){
            return "redirect:/admin/login";
        }
        try {

            Page<Plane> planes = planeService.findAll(pageNo, pageSize, sortField, sortDir);
            long startCount = (long) (pageNo - 1) * pageSize + 1;
            long endCount = startCount + pageSize -1;
            if (endCount > planes.getTotalElements()) {
                endCount = planes.getTotalElements();
            }
            String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

            model.addAttribute("reverseSortDir", reverseSortDir);
            model.addAttribute("planes", planes);
            model.addAttribute("currentPage", pageNo);
            model.addAttribute("totalPages", planes.getTotalPages());
            model.addAttribute("startCount", startCount);
            model.addAttribute("endCount", endCount);
            model.addAttribute("totalItems", planes.getTotalElements());
            model.addAttribute("sortField", sortField);
            model.addAttribute("sortDir", sortDir);
            model.addAttribute("keyword", null);
            return "admin/plane/ListPlanes";
        } catch(Exception e) {
            return "redirect:/admin/listPlane";
        }
    }

    @GetMapping("/admin/addPlane")
    public String getAddPlane(@ModelAttribute("airlineCompany") AirlineCompany airlineCompany,
                              ModelMap modelMap,
                              HttpServletRequest request
    ) {
        HttpSession session = request.getSession();
        if((int) session.getAttribute("role") < 2 || session.getAttribute("role")==null){
            return "redirect:/admin/login";
        }
        Iterable<AirlineCompany> airlineCompanies = airlineCompanyService.findAll();
        modelMap.addAttribute("airlineCompanies", airlineCompanies);
        return "admin/plane/AddPlane";
    }

    @PostMapping("/admin/addPlane")
    public String addPlane(@ModelAttribute("plane") Plane plane,
                              ModelMap modelMap,
                              RedirectAttributes redirectAttributes,
                              HttpServletRequest request) {
        String slSeat = request.getParameter("slSeat");

        try{
            if(isNullorEmpty(plane.getPlane_name().trim())) {
                redirectAttributes.addFlashAttribute("planeName", "Tên máy bay không được để trống!");
                return "redirect:/admin/addPlane";
            }
            if(slSeat.isEmpty() || slSeat.equals(null) || slSeat.equals("")) {
                redirectAttributes.addFlashAttribute("quantitySeat", "Số lượng ghế không được để trống!");
                return "redirect:/admin/addPlane";
            }
            if(Integer.parseInt(slSeat)<1) {
                redirectAttributes.addFlashAttribute("quantitySeat", "Số lượng ghế ngồi phải lớn hơn 0!");
                return "redirect:/admin/addPlane";
            }
            plane.setQuantity(Integer.parseInt(slSeat));
            planeService.save(plane);
            Iterable<Plane> planes = planeService.findAll();
            modelMap.addAttribute("planes", planes);
            return "redirect:/admin/listPlane";
        } catch (Exception e) {
            Iterable<AirlineCompany> airlineCompanies = airlineCompanyService.findAll();
            modelMap.addAttribute("airlineCompanies", airlineCompanies);
            return "admin/plane/AddPlane";
        }
    }

    @GetMapping("/admin/updatePlane/{planeID}")
    public String getUpdatePlane(ModelMap modelMap,
                                 @PathVariable Integer planeID,
                                 HttpServletRequest request
    ) {
        HttpSession session = request.getSession();
        if((int) session.getAttribute("role") < 2 || session.getAttribute("role")==null){
            return "redirect:/admin/login";
        }

        Iterable<AirlineCompany> airlineCompanies = airlineCompanyService.findAll();
        modelMap.addAttribute("airlineCompanies", airlineCompanies);
        modelMap.addAttribute("plane", planeService.findById(planeID).get());
        return "admin/plane/UpdatePlane";
    }

    @PostMapping("/admin/updatePlane/{planeID}")
    public String updatePlane(@PathVariable Integer planeID,
                              @ModelAttribute("plane") Plane plane,
                              ModelMap modelMap,
                              HttpServletRequest request,
                              RedirectAttributes redirectAttributes
    ) {
        String slSeat = request.getParameter("slSeat");
        if (slSeat.isEmpty()) {
            slSeat="0";
        }
        try{
            if(planeService.findById(planeID).isPresent()) {
                Plane foundPlane = planeService.findById(plane.getPlaneID()).get();

                if(!isNullorEmpty(plane.getPlane_name().trim())) {
                    foundPlane.setPlane_name(plane.getPlane_name());
                }
                else if(isNullorEmpty(plane.getPlane_name().trim())) {
                    redirectAttributes.addFlashAttribute("planeName", "Tên máy bay không được để trống");
                    return "redirect:/admin/updatePlane/{planeID}";
                }

                if(Integer.parseInt(slSeat)>0) {
                    foundPlane.setQuantity(Integer.parseInt(slSeat));
                }
                else if(Integer.parseInt(slSeat)<1) {
                    redirectAttributes.addFlashAttribute("quantitySeat", "Số lượng ghế phải lớn hơn 0");
                    return "redirect:/admin/updatePlane/{planeID}";
                }

                if(plane.getAirlineCompany().getAirlineID()>0) {
                    foundPlane.setAirlineCompany(plane.getAirlineCompany());
                }

                planeService.save(foundPlane);
            }
        } catch (Exception e) {
            return "redirect:/admin/updatePlane/{planeID}";
        }

        Iterable<Plane> planes = planeService.findAll();
        modelMap.addAttribute("planes", planes);
        return "redirect:/admin/listPlane";
    }

    @PostMapping("/admin/deletePlane/{planeID}")
    public String deletePlane(@PathVariable Integer planeID,
                              @ModelAttribute("plane") Plane plane,
                              ModelMap modelMap) {
        planeService.deleteById(plane.getPlaneID());
        Iterable<Plane> planes = planeService.findAll();
        modelMap.addAttribute("planes", planes);
        return "redirect:/admin/listPlane";
    }

}
