package com.kttt.webbanve.controllers.client;

import com.kttt.webbanve.models.Customer;
import com.kttt.webbanve.services.CustomerServiceImpl;
import com.kttt.webbanve.services.MailSenderService;
import com.kttt.webbanve.services.UserServiceImpl;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.kttt.webbanve.models.User;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Random;

@Controller
public class AccountController {
    @Autowired
    UserServiceImpl us;
    @Autowired
    CustomerServiceImpl cu;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    MailSenderService mailSenderService;

    @GetMapping("/account")
    public String goToLogin(Model model){
        model.addAttribute("pageTitle","Đăng nhập");
        return "client/login";
    }
    @GetMapping("/account/signup")
    public String goToSignUp(Model model){
        model.addAttribute("user",new User());
        model.addAttribute("pageTitle","Đăng kí");
        return "client/signUp";
    }
    @PostMapping("/account/signup/save")
    public String signup(User user,RedirectAttributes ra,HttpServletRequest request){
        User u_exist = us.getAccount(user.getUsername());
        if(u_exist == null){
            Customer cus = new Customer();
            String email = request.getParameter("email");
            user.setPassword(bCryptPasswordEncoder.encode(request.getParameter("password")));
            if(request.getParameter("password").equals(request.getParameter("refill_password"))){
                cus.setUser(user);
                cus.setEmail(email);
                cu.createCustomer(cus);
                us.addAccount(user);
                ra.addFlashAttribute("messageSuccess","Đăng kí thành công !");
            }
            else
                ra.addFlashAttribute("messageError","Nhập lại sai mật khẩu !");
        }
        else{
            ra.addFlashAttribute("messageError","Tên đăng nhập đã tồn tại !");
        }
        return "redirect:/account/signup";
    }
    @GetMapping("/account/logout")
    public String logout(Model model,HttpServletRequest request){
        if(request.getSession().getAttribute("customer") != null)
            request.getSession().removeAttribute("customer");
        model.addAttribute("pageTitle","Homepage");
        return "client/index";
    }

    @PostMapping("/account/login")
    public String login(Model model, HttpServletRequest request, RedirectAttributes ra){
        Customer u;
        try{
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            u = cu.getCustomer(username);
            if(u != null){
                if(bCryptPasswordEncoder.matches(password,u.getUser().getPassword())){
                    model.addAttribute("pageTitle","Trang chủ");
                    model.addAttribute("messageLogin","Đăng nhập thành công!");
                    request.getSession().setAttribute("customer",u);
                    model.addAttribute("customer",u);
                    return "client/index";
                }
                else{
                    ra.addFlashAttribute("message","Sai mật khẩu !");
                    return "redirect:/account";
                }
            }
            else {
                ra.addFlashAttribute("message","Tài khoản không tồn tại !");
                return "redirect:/account";
            }

        }
        catch(Exception e){
            ra.addFlashAttribute("message",e.getMessage());
            return "redirect:/account";
        }
    }

    @GetMapping("/account/information/{customerID}")
    public String infoPage(@PathVariable("customerID") Integer cid,Model model){
        model.addAttribute("pageTitle","Thông tin cá nhân");
        Customer customer = cu.getByID(cid);
        model.addAttribute("customer",customer);
        return "client/personalInformation";
    }

    @PostMapping("/account/updateInfo")
    public String updateInfo(@Valid @ModelAttribute("customer") Customer customer, RedirectAttributes ra, BindingResult bindingResult){
            Integer cid =  customer.getCustomerID();
        try{
            if(bindingResult.hasErrors()){
                ra.addFlashAttribute("message","Cập nhật không thành công!");
            }
            else{
                cu.updateCustomer(customer,cid);
                ra.addFlashAttribute("message","Cập nhật thành công!");
            }
            return "redirect:/account/information/"+cid;

        }
        catch (Exception e){
            ra.addFlashAttribute("message",e.getMessage());
            return "redirect:/account/information/"+cid;
        }
    }

    @GetMapping("/account/changeForm")
    public String changeForm(Model model,HttpServletRequest request){
        if(request.getSession().getAttribute("customer") == null) {
            model.addAttribute("pageTitle","Trang chủ");
            return "client/index";
        }
        model.addAttribute("pageTitle","Thông tin cá nhân");
        return "client/changingPassword";
    }

    @PostMapping("/account/changePassword")
    public String changePass(Model model,HttpServletRequest request) throws MessagingException, IOException {
        User user_exist = us.getAccount(request.getParameter("username"));
        if(user_exist == null){
            model.addAttribute("error","Tài khoản không tồn tại!");
            model.addAttribute("pageTitle","Thông tin cá nhân");
            return "client/changingPassword";
        }
        user_exist.setPassword(bCryptPasswordEncoder.encode(request.getParameter("password")));
        int confirmCode = new Random().nextInt(100001,999999);
        mailSenderService.sendMailMessage(request.getParameter("email"),String.valueOf(confirmCode),"GOGO - Mã xác nhận yêu cầu đổi mật khẩu ");
        request.getSession().setAttribute("confirmCode",confirmCode);
        request.getSession().setAttribute("userChange",user_exist);
        model.addAttribute("confirm","Nhập mã xác nhận");
        model.addAttribute("pageTitle","Thông tin cá nhân");
        return "client/changingPassword";
    }

    @PostMapping("/account/confirmChange")
    public String confirmChange(Model model,HttpServletRequest request){
        String code = request.getParameter("confirmCode");
        if(!code.equals(request.getParameter("confirmCode"))){
            model.addAttribute("error","Mã xác nhận không đúng!");
            model.addAttribute("confirm","Nhập mã xác nhận");
            model.addAttribute("pageTitle","Thông tin cá nhân");
            return "client/changingPassword";
        }
        User user = (User) request.getSession().getAttribute("userChange");
        us.save(user);
        request.getSession().removeAttribute("userChange");
        request.getSession().removeAttribute("confirmCode");
        model.addAttribute("success","Đổi mật khẩu thành công");
        model.addAttribute("pageTitle","Thông tin cá nhân");
        return "client/changingPassword";
    }

    @PostMapping("/account/advertise")
    public String signUpAds(HttpServletRequest req,Model model){
        String email = req.getParameter("email-ads");
        Customer customer;
        if(email.trim().isEmpty()){
            model.addAttribute("error","Nhập địa chỉ email của bạn!");
        }
        else {
            customer  = cu.getCustomerByEmail(email);
            if(customer != null){
                model.addAttribute("error","Email đã tồn tại !");
            }
            model.addAttribute("success","Đăng kí thành công");
        }
        model.addAttribute("pageTitle","Trang chủ");
        return "client/index";
    }
}
