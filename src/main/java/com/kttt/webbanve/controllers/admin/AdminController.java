package com.kttt.webbanve.controllers.admin;

import com.kttt.webbanve.AbstractClass;
import com.kttt.webbanve.models.AirlineCompany;
import com.kttt.webbanve.models.Flight;
import com.kttt.webbanve.models.Plane;
import com.kttt.webbanve.models.User;
import com.kttt.webbanve.repositories.AirlineCompanyRepository;
import com.kttt.webbanve.repositories.FlightRepository;
import com.kttt.webbanve.repositories.PlaneRepository;
import com.kttt.webbanve.repositories.UserRepositories;
import com.kttt.webbanve.services.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class AdminController extends AbstractClass {
    @Autowired
    private UserServiceImpl userServiceImpl;
    @Autowired
    private PlaneRepository planeRepository;
    @Autowired
    private AirlineCompanyRepository airlineCompanyRepository;
    @Autowired
    private UserRepositories userRepositories;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @GetMapping("/admin/api")
    public String getAdmin(){
        return "admin/api";
    }

    @GetMapping("/admin")
    public String getAdminUrl(HttpServletRequest request,ModelMap modelMap){
        HttpSession session = request.getSession();
        if(session.getAttribute("role")==null){
            return "admin/loginAdmin";
        }
        List<Plane> planes = planeRepository.findAll();
        List<AirlineCompany> airlineCompanies = airlineCompanyRepository.findAll();
        List<User> users = userRepositories.findAll();
        modelMap.addAttribute("numberOfAircraft", planes.size());
        modelMap.addAttribute("numberOfAirlineCompany", airlineCompanies.size());
        modelMap.addAttribute("numberOfUserAccount", users.size());
        return "/admin/adminIndex";
    }

    @GetMapping("/admin/login")
    public String getLoginAdmin(HttpServletRequest request){
        return "/admin/loginAdmin";
    }

    @PostMapping("/admin/login")
    public String loginAdmin(HttpServletRequest request, RedirectAttributes redirectAttributes,ModelMap modelMap){
        try {
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            HttpSession session = request.getSession();
            User user = userServiceImpl.getAccount(username);
            if(isNullorEmpty(username) || isNullorEmpty(password)){
                redirectAttributes.addFlashAttribute("message", "Nhập đủ tên đăng nhập và mật khẩu");
                return "redirect:/admin/login";
            }
            if(user == null){
                redirectAttributes.addFlashAttribute("message", "Tài khoản admin không tồn tại!");
                return "redirect:/admin/login";
            }
            if(!bCryptPasswordEncoder.matches(password, user.getPassword())){
                redirectAttributes.addFlashAttribute("message", "Sai mật khẩu");
                return "redirect:/admin/login";
            }
            if(user.getRole()<1) {
                redirectAttributes.addFlashAttribute("message", "Từ chối đăng nhập");
                return "redirect:/admin/login";
            }
            if(user.getRole() == 2){
                request.getSession().setAttribute("ROLE_ADMIN",true);
            }
            session.setAttribute("user", user);
            session.setAttribute("username", username);
            session.setAttribute("role", user.getRole());
            List<User> users = userRepositories.findAll();
            modelMap.addAttribute("numberOfUserAccount", users.size());
//            return "redirect:/admin";
            return "/admin/adminIndex";
        } catch (Exception exception){
            System.out.println(exception);
            return "redirect:/admin/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("username");
        session.removeAttribute("user");
        session.removeAttribute("role");
        session.removeAttribute("ROLE_ADMIN");
        return "redirect:/admin/login";
    }

}
