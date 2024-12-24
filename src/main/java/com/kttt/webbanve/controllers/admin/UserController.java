package com.kttt.webbanve.controllers.admin;

import com.kttt.webbanve.models.Flight;
import com.kttt.webbanve.models.User;
import com.kttt.webbanve.services.UserService;
import jakarta.mail.Session;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/admin/userList")
    public String getFirstPage(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        if(session.getAttribute("role")==null){
            return "admin/loginAdmin";
        }
        return getAllUser(model, request,1, 5, "userID", "asc");
    }

    @GetMapping("/admin/userList/page/{pageNo}")
    public String getAllUser(Model model,
                             HttpServletRequest request,
                             @PathVariable(name = "pageNo") int pageNo,
                             @RequestParam(defaultValue = "5", required = false) int pageSize,
                             @RequestParam(defaultValue = "userID", required = false) String sortField,
                             @RequestParam(defaultValue = "asc", required = false) String sortDir
    ) {
        try {
            HttpSession session = request.getSession();
            if(session.getAttribute("role")=="1" || session.getAttribute("role")==null){
                return "admin/loginAdmin";
            }
            Page<User> users = userService.findAll(pageNo, pageSize, sortField, sortDir);

            long startCount = (long) (pageNo - 1) * pageSize + 1;
            long endCount = startCount + pageSize -1;
            if (endCount > users.getTotalElements()) {
                endCount = users.getTotalElements();
            }
            String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

            model.addAttribute("reverseSortDir", reverseSortDir);
            model.addAttribute("users", users);
            model.addAttribute("currentPage", pageNo);
            model.addAttribute("totalPages", users.getTotalPages());
            model.addAttribute("startCount", startCount);
            model.addAttribute("endCount", endCount);
            model.addAttribute("totalItems", users.getTotalElements());
            model.addAttribute("sortField", sortField);
            model.addAttribute("sortDir", sortDir);
            model.addAttribute("keyword", null);

            return "/admin/user/UserList";
        } catch (Exception e) {
            return "redirect:/admin/userList";
        }
    }

    @GetMapping("admin/updateUser/{userId}")
    public String updateFlight(@PathVariable Integer userId,
                               ModelMap modelMap,
                               HttpServletRequest request) {
        HttpSession session = request.getSession();
        if((int) session.getAttribute("role") < 2 || session.getAttribute("role")==null){
            return "redirect:/admin/login";
        }
        modelMap.addAttribute("user", userService.findById(userId).get());
        return "admin/user/UpdateUser";
    }

    @PostMapping("admin/updateUser/{userId}")
    public String updateFlight(@ModelAttribute("user") User user,
                               ModelMap modelMap,
                               HttpServletRequest request,
                               RedirectAttributes redirectAttributes,
                               @PathVariable Integer userId
    ){
        if(userService.findById(userId).isPresent()) {
            User userFound = userService.findById(userId).get();
            userFound.setRole(user.getRole());
            userService.save(userFound);
        }
        return "redirect:/admin/userList";
    }

    @PostMapping("admin/deleteUser/{userId}")
    public String deleteUser(@PathVariable int userId, HttpServletRequest request) {
        HttpSession
                session = request.getSession();
        if(session.getAttribute("role")==null || (int)session.getAttribute("role") == 0){
            return "admin/loginAdmin";
        }
        userService.deleteById(userId);
        return "redirect:/admin/userList";
    }

    @GetMapping("/admin/addUser")
    public String addUserForm(){
        return "/admin/user/addUser";
    }
    @PostMapping("/admin/addUser/save")
    public String addUser(HttpServletRequest request, RedirectAttributes ra){
        if(userService.getAccount(request.getParameter("username").trim()) != null){
            return "redirect:/admin/userList";
        }
        try{
            User user = new User();
            user.setUsername(request.getParameter("username"));
            user.setPassword(request.getParameter("password"));
            user.setRole(Integer.parseInt(request.getParameter("role")));
            userService.addAccount(user);
            ra.addFlashAttribute("success","Thêm thành công");
            return "redirect:/admin/addUser";
        }catch (Exception e){
            e.printStackTrace();
            ra.addFlashAttribute("error",e.getMessage());
            return "redirect:/admin/addUser";
        }
    }
}
