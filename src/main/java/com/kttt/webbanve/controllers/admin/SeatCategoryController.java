package com.kttt.webbanve.controllers.admin;

import com.kttt.webbanve.models.SeatCategory;
import com.kttt.webbanve.repositories.SeatCategoryRepository;
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

@Controller
public class SeatCategoryController {
    @Autowired
    private SeatCategoryRepository seatCategoryRepository;

    @GetMapping("/admin/seatCategoryList")
    public String getFirstPage(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        if(session.getAttribute("role")==null || (int)session.getAttribute("role") == 0){
            return "admin/loginAdmin";
        }
        return getAllSeatCategory(model, request,1, 5, "seatCategoryID", "asc");
    }

    @GetMapping("/admin/seatCategoryList/page/{pageNo}")
    public String getAllSeatCategory(Model model,
                                     HttpServletRequest request,
                                     @PathVariable(name = "pageNo") int pageNo,
                                     @RequestParam(defaultValue = "5", required = false) int pageSize,
                                     @RequestParam(defaultValue = "seatCategoryID", required = false) String sortField,
                                     @RequestParam(defaultValue = "asc", required = false) String sortDir
    ) {
        HttpSession session = request.getSession();
        if(session.getAttribute("role")=="1" || session.getAttribute("role")==null){
            return "admin/loginAdmin";
        }
        Pageable pageable = PageRequest.of(pageNo-1, pageSize, Sort.by(sortField));
        Page<SeatCategory> seatCategories = seatCategoryRepository.findAll(pageable);

        long startCount = (long) (pageNo - 1) * pageSize + 1;
        long endCount = startCount + pageSize -1;
        if (endCount > seatCategories.getTotalElements()) {
            endCount = seatCategories.getTotalElements();
        }
        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

        model.addAttribute("reverseSortDir", reverseSortDir);
        model.addAttribute("seatCategories", seatCategories);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", seatCategories.getTotalPages());
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalItems", seatCategories.getTotalElements());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("keyword", null);
        return "admin/seatCategory/SeatCategoryList";
    }

    @GetMapping("/admin/addSeatCategory")
    public String getAddForm(HttpServletRequest request) {
        HttpSession session = request.getSession();
        if((int) session.getAttribute("role") < 2 || session.getAttribute("role")==null){
            return "redirect:/admin/login";
        }
        return "admin/seatCategory/AddSeatCategory";
    }

    @PostMapping("/admin/addSeatCategory")
    public String addSeatCategory(@ModelAttribute("seatCategory") SeatCategory seatCategory) {
        seatCategoryRepository.save(seatCategory);
        return "redirect:/admin/seatCategoryList";
    }

    @GetMapping("/admin/updateSeatCategory/{id}")
    public String getUpdateForm(ModelMap modelMap, @PathVariable int id, HttpServletRequest request) {
        HttpSession session = request.getSession();
        if((int) session.getAttribute("role") < 2 || session.getAttribute("role")==null){
            return "redirect:/admin/login";
        }
        modelMap.addAttribute("seatCategory", seatCategoryRepository.findById(id).get());
        return "admin/seatCategory/UpdateSeatCategory";
    }

    @PostMapping("/admin/updateSeatCategory/{id}")
    public String updateSeatCategory(@PathVariable int id, @ModelAttribute SeatCategory seatCategory) {
        SeatCategory foundSeatCategory = seatCategoryRepository.findById(id).get();
        foundSeatCategory.setCategoryName(seatCategory.getCategoryName());
        foundSeatCategory.setLuggageAttach(seatCategory.getLuggageAttach());
        foundSeatCategory.setFeeCategory(seatCategory.getFeeCategory());
        seatCategoryRepository.save(foundSeatCategory);
        return "redirect:/admin/seatCategoryList";
    }
}
