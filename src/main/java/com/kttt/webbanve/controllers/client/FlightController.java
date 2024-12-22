package com.kttt.webbanve.controllers.client;

import com.kttt.webbanve.models.*;
import com.kttt.webbanve.models.supportModels.FlightSelected;
import com.kttt.webbanve.repositories.*;
import com.kttt.webbanve.services.*;
import com.kttt.webbanve.services.GeneratePdfService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.ArrayList;

@Controller
public class FlightController {
    @Autowired
    FlightServiceImpl flightService;
    @Autowired
    SeatCategoryRepositories sr;
    @Autowired
    SeatRepositories seatRepositoriesr;
    @Autowired
    LuggageRepositories lr;
    @Autowired
    FlightRepositories fr;
    @Autowired
    PlaneRepositories pr;
    @Autowired
    CustomerRepositories cr;
    @Autowired
    CustomerServiceImpl customerService;
    @Autowired
    OrderRepositories or;
    @Autowired
    TicketRepositories tr;
    @Autowired
    SpringTemplateEngine springTemplateEngine;
    @Autowired
    GeneratePdfService generatePdfService;
    @Autowired
    DataMapper mapper;
    @Autowired
    MailSenderService mss;
    @Autowired
    PlaneFlightServiceImpl planeFlightService;
    @Autowired
    PlaneService planeService;
    @Autowired
    TicketService ticketService;
    @Autowired
    OrderInfoService orderInfoService;
    @GetMapping("/flights")
    public String listFlight(Model model,HttpServletRequest request){
        ArrayList<Flight> flights = (ArrayList<Flight>) flightService.getAllFlights();
        model.addAttribute("pageTitle","Danh sách chuyến bay");
        model.addAttribute("list_flight",flights);
        if(request.getSession().getAttribute("seatClass") != null){
            SeatCategory sl = (SeatCategory) request.getSession().getAttribute("seatClass");
            if(request.getSession().getAttribute("totalBill") != null){
                long totalBill = (long)request.getSession().getAttribute("totalBill");
                totalBill -= sl.getFeeCategory();
                request.getSession().setAttribute("totalBill",totalBill);
            }
        }
        return "client/listFlights";
    }

    @PostMapping("/flights/search")
    public String listFlight2(Model model, HttpServletRequest request){
        if(request.getSession().getAttribute("flightSelected") != null){
            request.getSession().removeAttribute("flightSelected");
        }
        if(request.getSession().getAttribute("planes1") != null){
            request.getSession().removeAttribute("planes1");
        }
        if(request.getSession().getAttribute("planes2") != null){
            request.getSession().removeAttribute("planes2");
        }
        if(request.getSession().getAttribute("planes") != null){
            request.getSession().removeAttribute("planes");
        }
        if(request.getSession().getAttribute("adults") != null){
            request.getSession().removeAttribute("adults");
        }
        if(request.getSession().getAttribute("child") != null){
            request.getSession().removeAttribute("child");
        }
        if(request.getSession().getAttribute("seatClass") != null){
            request.getSession().removeAttribute("seatClass");
        }
        String date_flight = request.getParameter("txtDateMove");
        String date_return = request.getParameter("txtDateReturn");
        String departing_from = request.getParameter("txtFrom");
        String arriving_at = request.getParameter("txtTo");
        long txtAdults = Long.parseLong(request.getParameter("txtAdults"));
        long txtChild;
        if(request.getParameter("txtChilds") != "")
            txtChild = Long.parseLong(request.getParameter("txtChilds"));
        else
            txtChild = 0;
        String rd = request.getParameter("rdCategoryTicket");
        SeatCategory sl = sr.getSeatCategoryByCategoryName(request.getParameter("slSeatClass"));
        model.addAttribute("pageTitle","Danh sách chuyến bay");
        if(rd.equals("rt")){
            Page<Flight> flights1 = flightService.findFlightByForm(date_flight,departing_from,arriving_at,1);
            Page<Flight> flights2 = flightService.findFlightByForm(date_return,arriving_at,departing_from,1);
            if(flights1.isEmpty() || flights2.isEmpty()){
                model.addAttribute("error","Không tìm thấy chuyến bay !");
                return "client/listFlights";
            }
            Page<PlaneFlight> planeFlightPage1 = planeFlightService.getAllFlight(flights1.getContent().get(0).getFlightID(),1);
            Page<PlaneFlight> planeFlightPage2 = planeFlightService.getAllFlight(flights2.getContent().get(0).getFlightID(),1);
            int totalItems1 = planeFlightPage1.getNumberOfElements();
            int totalPages1 = planeFlightPage1.getTotalPages();
            int totalItems2 = planeFlightPage2.getNumberOfElements();
            int totalPages2 = planeFlightPage2.getTotalPages();
            request.getSession().setAttribute("totalPages1",totalPages1);
            request.getSession().setAttribute("totalItems1",totalItems1);
            request.getSession().setAttribute("totalPages2",totalPages2);
            request.getSession().setAttribute("totalItems2",totalItems2);
            request.getSession().setAttribute("planes1",planeFlightService.convertToListFlight(planeFlightPage1.getContent()));
            request.getSession().setAttribute("planes2",planeFlightService.convertToListFlight(planeFlightPage2.getContent()));
            model.addAttribute("departing_from",departing_from);
            model.addAttribute("arriving_at",arriving_at);
            request.getSession().setAttribute("adults",txtAdults);
            request.getSession().setAttribute("child",txtChild);
            request.getSession().setAttribute("departing_from",departing_from);
            request.getSession().setAttribute("date_flight",date_flight);
            request.getSession().setAttribute("date_return",date_return);
            request.getSession().setAttribute("arriving_at",arriving_at);
            request.getSession().setAttribute("seatClass",sl);
            request.getSession().setAttribute("categoryTicket",2);
            model.addAttribute("pageNum",1);
            return "client/listFlights";
        }
        else{
            Page<Flight> flights = flightService.findFlightByForm(date_flight,departing_from,arriving_at,1);
            if(flights.isEmpty()){
                model.addAttribute("error","Không tìm thấy chuyến bay !");
                return "client/listFlights";
            }
            Page<PlaneFlight> planeFlightPage = planeFlightService.getAllFlight(flights.getContent().get(0).getFlightID(),1);
            if(planeFlightPage.getNumberOfElements() > 0)
                request.getSession().setAttribute("planes",planeFlightService.convertToListFlight(planeFlightPage.getContent()));
            int totalItems = planeFlightPage.getNumberOfElements();
            int totalPages = planeFlightPage.getTotalPages();
            request.getSession().setAttribute("totalPages",totalPages);
            request.getSession().setAttribute("totalItems",totalItems);
            model.addAttribute("date_flight",date_flight);
            model.addAttribute("departing_from",departing_from);
            model.addAttribute("arriving_at",arriving_at);
            request.getSession().setAttribute("departing_from",departing_from);
            request.getSession().setAttribute("date_flight",date_flight);
            request.getSession().setAttribute("arriving_at",arriving_at);
            request.getSession().setAttribute("adults",txtAdults);
            request.getSession().setAttribute("child",txtChild);
            request.getSession().setAttribute("seatClass",sl);
            model.addAttribute("pageNum",1);
            request.getSession().removeAttribute("categoryTicket");
            return "client/listFlights";
        }
    }

    @GetMapping("/flights/search/v1/page/{pageNumber}")
    public String flightPagingV1(@PathVariable int pageNumber, Model model,HttpServletRequest request){
        String departing_from = (String) request.getSession().getAttribute("departing_from");
        String date_flight = (String) request.getSession().getAttribute("date_flight");
        String arriving_at = (String) request.getSession().getAttribute("arriving_at");
        Page<Flight> flights = flightService.findFlightByForm(date_flight,departing_from,arriving_at,1);
        Page<PlaneFlight> planeFlightPage = planeFlightService.getAllFlight(flights.getContent().get(0).getFlightID(),pageNumber);
        if(planeFlightPage.getNumberOfElements() > 0)
            request.getSession().setAttribute("planes",planeFlightService.convertToListFlight(planeFlightPage.getContent()));
        model.addAttribute("pageTitle","Danh sách chuyến bay");
        model.addAttribute("pageNum",pageNumber);
        return "client/listFlights";
    }

    @GetMapping("/flights/search/v2/page/{pageNumber}")
    public String flightPagingV2(@PathVariable int pageNumber, Model model, HttpServletRequest request){
        String departing_from = (String) request.getSession().getAttribute("departing_from");
        String date_flight = (String) request.getSession().getAttribute("date_flight");
        String arriving_at = (String) request.getSession().getAttribute("arriving_at");
        String date_return = (String) request.getSession().getAttribute("date_return");
        Page<Flight> flights1 = flightService.findFlightByForm(date_flight,departing_from,arriving_at,1);
        Page<PlaneFlight> planeFlightPage1 = planeFlightService.getAllFlight(flights1.getContent().get(0).getFlightID(),pageNumber);
        Page<Flight> flights2 = flightService.findFlightByForm(date_return,arriving_at,departing_from,1);
        Page<PlaneFlight> planeFlightPage2 = planeFlightService.getAllFlight(flights2.getContent().get(0).getFlightID(),pageNumber);
        request.getSession().setAttribute("planes1",planeFlightService.convertToListFlight(planeFlightPage1.getContent()));
        request.getSession().setAttribute("planes2",planeFlightService.convertToListFlight(planeFlightPage2.getContent()));
        model.addAttribute("pageTitle","Danh sách chuyến bay");
        model.addAttribute("pageNum",pageNumber);
        return "client/listFlights";
    }

    public long totalFee(ArrayList<FlightSelected> flightSelecteds){
        long total=0;
        for (FlightSelected f :
                flightSelecteds) {
            total += f.getFlight().getFeeFlight() + f.getFlight().getPlanes().get(0).getAirlineCompany().getAirline_fee();
        }
        return total;
    }

    @GetMapping("/flights/takeFlightV2/{fid}&{pid}")
    public String selectedFlight2(@PathVariable(name = "fid")int fid,@PathVariable(name = "pid")int pid,HttpServletRequest request,Model model){
        SeatCategory sl = sr.getSeatCategoryByCategoryName("Thương gia");
        request.getSession().setAttribute("seatClass",sl);
        long adult = 1;
        request.getSession().setAttribute("adults",adult);
        long child = 0;
        request.getSession().setAttribute("child",child);
        Flight flight = flightService.getFlightByID(fid);
        Plane plane = pr.getPlaneByPlaneID(pid);
        ArrayList<Plane> planes = new ArrayList<>();
        planes.add(plane);
        flight.setPlanes(planes);
        if(request.getSession().getAttribute("categoryTicket") != null){
            request.getSession().removeAttribute("categoryTicket");
        }
        ArrayList<FlightSelected> flightSelected = new ArrayList<>();
        FlightSelected fs = new FlightSelected();
        fs.setFlight(flight);
        flightSelected.add(fs);
        request.getSession().setAttribute("flightSelected", flightSelected);
        request.getSession().setAttribute("totalBill",(long)(totalFee(flightSelected)*(adult + child*0.8) + sl.getFeeCategory()*(adult + child)));
        model.addAttribute("pageTitle","Danh sách chuyến bay");
        return "client/listFlights";
    }

    @GetMapping("/flights/takeFlight/{fid}&{pid}")
    public String selectedFlight(@PathVariable(name = "fid")int fid,@PathVariable(name = "pid")int pid,HttpServletRequest request,Model model){
        Flight flight = flightService.getFlightByID(fid);
        Plane plane = pr.getPlaneByPlaneID(pid);
        ArrayList<Plane> planes = new ArrayList<>();
        planes.add(plane);
        flight.setPlanes(planes);
        if(request.getSession().getAttribute("categoryTicket") != null){
            if(request.getSession().getAttribute("flightSelected") == null){
                ArrayList<FlightSelected> flightSelected = new ArrayList<>();
                FlightSelected fs = new FlightSelected();
                fs.setFlight(flight);
                flightSelected.add(fs);
                request.getSession().setAttribute("flightSelected",flightSelected);
            }
            else{
                ArrayList<FlightSelected> flightSelected = (ArrayList<FlightSelected>) request.getSession().getAttribute("flightSelected");
                if(flightSelected.size()>1){
                    model.addAttribute("error","Không thể chọn nhiều hơn 2 chuyến bay!");
                }
                else{
                    for (FlightSelected fl :
                            flightSelected) {
                        if(fl.getFlight().getFlightID() == fid) {
                            model.addAttribute("error", "Chuyến bay đã được chọn!");
                            model.addAttribute("pageTitle","Danh sách chuyến bay");
                            return "client/listFlights";
                        }
                    }
                    FlightSelected fs = new FlightSelected();
                    fs.setFlight(flight);
                    flightSelected.add(fs);
                    request.getSession().setAttribute("flightSelected",flightSelected);
                }
            }
        }
        else{
            if(request.getSession().getAttribute("flightSelected") != null){
                ArrayList<FlightSelected> flightSelected = (ArrayList<FlightSelected>) request.getSession().getAttribute("flightSelected");
                if(flightSelected.size()>0) {
                    model.addAttribute("error", "Xóa lựa để chọn lại!");
                }
                else{
                    FlightSelected fs = new FlightSelected();
                    fs.setFlight(flight);
                    flightSelected.add(fs);
                    request.getSession().setAttribute("flightSelected", flightSelected);
                }
            }
            else {
                ArrayList<FlightSelected> flightSelected = new ArrayList<>();
                FlightSelected fs = new FlightSelected();
                fs.setFlight(flight);
                flightSelected.add(fs);
                request.getSession().setAttribute("flightSelected", flightSelected);
            }
        }
        model.addAttribute("pageTitle","Danh sách chuyến bay");
        return "client/listFlights";
    }

    @GetMapping("/flights/delSelected")
    public String delSelected(Model model,HttpServletRequest request){
        model.addAttribute("pageTitle","Flights");
        if(request.getSession().getAttribute("flightSelected") != null) {
            request.getSession().removeAttribute("flightSelected");
            if(request.getSession().getAttribute("totalBill") != null){
                request.getSession().removeAttribute("totalBill");
            }
            if(request.getSession().getAttribute("passengers") != null){
                request.getSession().removeAttribute("passengers");
            }
        }
        return "client/listFlights";
    }

    @GetMapping("/flights/delFlight/{fid}")
    public String delFlight(@PathVariable(name = "fid")int fid,HttpServletRequest request,Model model){
        try{
            SeatCategory sl = (SeatCategory) request.getSession().getAttribute("seatClass");
            Flight flight = flightService.getFlightByID(fid);
            ArrayList<FlightSelected> flightSelected = (ArrayList<FlightSelected>) request.getSession().getAttribute("flightSelected");

            for (FlightSelected fl :
                    flightSelected) {
                if(fl.getFlight().getFlightID() == flight.getFlightID()) {
                    flightSelected.remove(fl);
                }
            }
            request.getSession().setAttribute("flightSelected",flightSelected);
            model.addAttribute("pageTitle","Danh sách chuyến bay");
        }
        catch (Exception e){
            model.addAttribute("error",e.getMessage());
            model.addAttribute("pageTitle","Danh sách chuyến bay");
        }
        return "client/listFlights";
    }

    @GetMapping("/flights/fillInfor")
    public String goToInfor(Model model,HttpServletRequest request){
        ArrayList<FlightSelected> flightSelecteds = (ArrayList<FlightSelected>) request.getSession().getAttribute("flightSelected");
        if(flightSelecteds.size() < 2 && request.getSession().getAttribute("categoryTicket") != null){
            model.addAttribute("pageTitle","Danh sách chuyến bay");
            model.addAttribute("error","Bạn phải chọn 2 chuyến bay!");
            return "client/listFlights";
        }
        model.addAttribute("pageTitle","Thông tin hành khách");
        long quanAdults = (long)request.getSession().getAttribute("adults") + (long)request.getSession().getAttribute("child");
        ArrayList<Integer> adults = new ArrayList<>();
        //---total-bill-----
        SeatCategory sl = (SeatCategory) request.getSession().getAttribute("seatClass");
        long adult = (long) request.getSession().getAttribute("adults");
        long child = (long) request.getSession().getAttribute("child");
        request.getSession().setAttribute("totalBill",(long)(totalFee(flightSelecteds)*(adult + child*0.8) + sl.getFeeCategory()*(adult + child)*flightSelecteds.size()));
        //-------------------
        for(int i=0;i<quanAdults;i++)
            adults.add(i);
        model.addAttribute("adults",adults);
        ArrayList<Luggage> luggages = lr.findAll();
        model.addAttribute("luggages",luggages);
        return "client/inforTicket";
    }

    @PostMapping("/flights/saveInfor")
    public String saveInfor(Model model, HttpServletRequest request, RedirectAttributes ra){
        try{
            long totalBill = (long) request.getSession().getAttribute("totalBill");
            long quanAdults = (long)request.getSession().getAttribute("adults") + (long)request.getSession().getAttribute("child");
            ArrayList<FlightSelected> flightSelected = (ArrayList<FlightSelected>) request.getSession().getAttribute("flightSelected");
            ArrayList<FlightSelected> flightSelected_new = new ArrayList<>();
            for(int i=0;i<quanAdults;i++){
                String fullname = request.getParameter("fullname"+i);
                String sex = request.getParameter("sex"+i);
                String birthday = request.getParameter("birthday"+i);
                String email = request.getParameter("email"+i);
                if(cr.getCustomerByEmail(email)!=null){
                    model.addAttribute("pageTitle","Chọn ghế ngồi");
                    ra.addFlashAttribute("error","Email "+ email +" đã tồn tại !");
                    return "redirect:/flights/fillInfor";
                }
                String phone = request.getParameter("phone"+i);
                String luggageID = request.getParameter("luggage"+i);
                Luggage luggage = lr.getLuggagesByLuggageID(Integer.parseInt(luggageID));
                Customer cus = new Customer();
                cus.setEmail(email);
                cus.setSex(sex);
                cus.setPhone(phone);
                cus.setFullname(fullname);
                cus.setBirthday(birthday);
                cr.save(cus);
                for(FlightSelected f : flightSelected){
                    FlightSelected fs = new FlightSelected();
                    fs.setFlight(f.getFlight());
                    fs.setCustomer(cr.getCustomerByEmail(email));
                    fs.setLuggage(luggage);
                    if(i==quanAdults-1 && (long)request.getSession().getAttribute("child")>0){
                        fs.setAirfares((long) (f.getFlight().getFeeFlight()*0.8 + luggage.getCost()));
                    }
                    totalBill += (long)luggage.getCost();
                    flightSelected_new.add(fs);
                }
            }
            request.getSession().setAttribute("flightSelected",flightSelected_new);
            request.getSession().setAttribute("totalBill",totalBill);
            model.addAttribute("pageTitle","Chọn ghế ngồi");
            SeatCategory category = (SeatCategory) request.getSession().getAttribute("seatClass");
            for (FlightSelected f :
                    flightSelected_new) {
                for (Plane p :
                        f.getFlight().getPlanes()) {
                    ArrayList<Seat> seats = seatRepositoriesr.getSeatsBySeatCategory_CategoryNameAndPlane_PlaneID(category.getCategoryName(),p.getPlaneID());
                    f.setSeats(seats);
                }
            }
            request.getSession().setAttribute("flightSelected",flightSelected_new);
            model.addAttribute("quantityTicket",flightSelected_new.size()-1);
            return "client/selectSeat";
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
            ra.addFlashAttribute("error",e.getMessage());
            return "redirect:/flights/fillInfor";
        }
    }

    @GetMapping("/flights/saveInfor/{sid}&{fid}&{cemail}")
    public String selectSeat(@PathVariable("sid") int sid,@PathVariable("fid") int fid,@PathVariable("cemail") String cemail,Model model,HttpServletRequest request){
        ArrayList<FlightSelected> flightSelecteds = (ArrayList<FlightSelected>) request.getSession().getAttribute("flightSelected");
        Seat seat = seatRepositoriesr.getSeatBySeatID(sid);
        Flight flight = fr.getFlightByFlightID(fid);
        int dem = 0;
        if(seat.getStatus() > 0){
            model.addAttribute("error","Ghế đã được đặt!");
            model.addAttribute("pageTitle","Chọn ghế ngồi");
            return "client/selectSeat";
        }

        for(FlightSelected f : flightSelecteds){
            if (f.getSeat() != null && sid == f.getSeat().getSeatID()) {
                model.addAttribute("error", "Ghế đã được đặt!");
                model.addAttribute("pageTitle","Chọn ghế ngồi");
                return "client/selectSeat";
            }
            if(f.getSeat() != null) {
                if (f.getFlight().getDepartingFrom().equals(flight.getDepartingFrom()) && f.getFlight().getArrivingAt().equals(flight.getArrivingAt()) && f.getCustomer().getEmail().equals(cemail)) {
                    model.addAttribute("error", "Ghế đã được đặt!");
                    model.addAttribute("pageTitle","Select seat");
                    return "client/selectSeat";
                }
                dem ++;
            }
        }
        for(FlightSelected f : flightSelecteds){
            if(f.getSeat() == null) {
                f.setSeat(seat);
                seat.setStatus(3);
                f.setAirfares(f.getAirfares() + seat.getSeatCategory().getFeeCategory() + f.getFlight().getFeeFlight());
                for (Plane p :
                        f.getFlight().getPlanes()) {
                    ArrayList<Seat> seats = seatRepositoriesr.getSeatsBySeatCategory_CategoryNameAndPlane_PlaneID(seat.getSeatCategory().getCategoryName(),p.getPlaneID());
                    f.setSeats(seats);
                }
                break;
            }
        }
        if(dem == flightSelecteds.size())
            model.addAttribute("error","Xóa lựa chọn cũ để chọn lại!");
        request.getSession().setAttribute("flightSelected",flightSelecteds);
        model.addAttribute("seatPo",sid);
        model.addAttribute("pageTitle","Chọn ghế ngồi");
        return "client/selectSeat";
    }
    @GetMapping("/flights/changeInfo/{sid}")
    public String changeInfo(@PathVariable("sid") int sid,Model model,HttpServletRequest request){
        ArrayList<FlightSelected> flightSelecteds = (ArrayList<FlightSelected>) request.getSession().getAttribute("flightSelected");
        Seat seat = seatRepositoriesr.getSeatBySeatID(sid);
        SeatCategory sl = (SeatCategory) request.getSession().getAttribute("seatClass");
        for(FlightSelected f : flightSelecteds){
            if(f.getSeat().getSeatID() == sid) {
                seat.setStatus(0);
                f.setSeat(null);
                f.setAirfares(f.getAirfares() + seat.getSeatCategory().getFeeCategory() + f.getFlight().getFeeFlight());
                seatRepositoriesr.save(seat);
                f.setSeats(null);
                for (Plane p :
                        f.getFlight().getPlanes()) {
                    ArrayList<Seat> seats = seatRepositoriesr.getSeatsBySeatCategory_CategoryNameAndPlane_PlaneID(sl.getCategoryName(),p.getPlaneID());
                    f.setSeats(seats);
                }
                break;
            }
        }
        request.getSession().setAttribute("flightSelected",flightSelecteds);
        model.addAttribute("pageTitle","Chọn ghế ngồi");
        return "client/selectSeat";
    }

    @GetMapping("/flights/displayPayment")
    public String displayPayment(Model model,HttpServletRequest request){
        ArrayList<FlightSelected> flightSelecteds = (ArrayList<FlightSelected>) request.getSession().getAttribute("flightSelected");
        SeatCategory sl = (SeatCategory) request.getSession().getAttribute("seatClass");
        for (FlightSelected f :
                flightSelecteds) {
            if(f.getSeat() == null){
                model.addAttribute("error","Chọn ít nhất "+flightSelecteds.size()+" ghế ngồi!");
                for (Plane p :
                        f.getFlight().getPlanes()) {
                    ArrayList<Seat> seats = seatRepositoriesr.getSeatsBySeatCategory_CategoryNameAndPlane_PlaneID(sl.getCategoryName(),p.getPlaneID());
                    f.setSeats(seats);
                }
                request.getSession().setAttribute("flightSelected",flightSelecteds);
                model.addAttribute("pageTitle","Chọn ghế ngồi");
                return "client/selectSeat";
            }
        }
        for (FlightSelected f :
                flightSelecteds) {
            Seat seat = f.getSeat();
            seat.setStatus(2);
            seatRepositoriesr.save(seat);
        }
        model.addAttribute("pageTitle","Thanh toán");
        return "client/payment";
    }

    @GetMapping("/flights/paymentSuccess")
    public String displayReturn(
            @RequestParam("vnp_TxnRef") String orderCode,
            @RequestParam("vnp_Amount") long totalBill,
            @RequestParam("vnp_ResponseCode") String responseCode,
            @RequestParam("vnp_TransactionNo") String transNo,
            Model model,
            HttpServletRequest request
    ) throws Exception {
        if(responseCode.equals("00")){

            ArrayList<FlightSelected> flightSelecteds = (ArrayList<FlightSelected>) request.getSession().getAttribute("flightSelected");
            for(FlightSelected f : flightSelecteds){
                Seat seat = f.getSeat();
                seat.setStatus(1);
                seatRepositoriesr.save(seat);
            }
            OrderInfo order = or.save(or.getOrderInfoByQrCode(orderCode));
            //Generate html to pdf
            ArrayList<Ticket> tickets = (ArrayList<Ticket>) request.getSession().getAttribute("tickets");
            String processedHtml = "";
            Context dataContext = mapper.setData(order,tickets);
            processedHtml = springTemplateEngine.process("client/orderDetail",dataContext);
            generatePdfService.htmlToPdf(processedHtml,order.getQrCode());
            generatePdfService.addImgToPDF(order.getQrCode());
            mss.sendMailWithAttachment(order.getCustomer().getEmail(),"Cảm ơn bạn đã tin tưởng đặt vé tại đại lý GOGO !\nXem chi tiết đơn hàng phía dưới.","Chi tiết đơn hàng",order.getQrCode());
            model.addAttribute("order",order);
            model.addAttribute("totalBill",totalBill);
            model.addAttribute("pageTitle","Thanh toán thành công");
            request.getSession().removeAttribute("flightSelected");
            request.getSession().setAttribute("transNo",transNo);
            return "client/paySuccess";
        }
        else{
            ArrayList<FlightSelected> flightSelecteds = (ArrayList<FlightSelected>) request.getSession().getAttribute("flightSelected");
            for(FlightSelected f : flightSelecteds){
                Seat seat = f.getSeat();
                seat.setStatus(0);
                seatRepositoriesr.save(seat);
            }
            model.addAttribute("pageTitle","Payment");
            return "redirect:/flights/displayPayment";
        }
    }

    @GetMapping("/flights/searchOrder")
    public String searchOrder(Model model,HttpServletRequest request){
        if(request.getSession().getAttribute("order") != null && request.getSession().getAttribute("tickets") != null){
            model.addAttribute("order",request.getSession().getAttribute("order"));
            model.addAttribute("tickets",request.getSession().getAttribute("tickets"));
            request.getSession().removeAttribute("order");
            request.getSession().removeAttribute("tickets");
            model.addAttribute("success","Thành công");
        }
        model.addAttribute("pageTitle","Cancel flights");
        return "client/searchOrder";
    }

    @PostMapping("/flights/cancel")
    public String cancel(HttpServletRequest request,Model model){
        String orderID = request.getParameter("orderID");
        OrderInfo orderInfo = orderInfoService.getOrderByID(Integer.parseInt(orderID));
        orderInfo.setStatus(1);
        orderInfoService.saveOrder(orderInfo);
        model.addAttribute("success","Send requirement successfully!");
        model.addAttribute("pageTitle","Cancel flights");
        return "client/searchOrder";
    }

    @GetMapping("/flights/showOrder/fail")
    public String showFail(Model model){
        model.addAttribute("error","Hóa đơn không tồn tại!");
        model.addAttribute("pageTitle","Hủy chuyến bay");
        return "client/searchOrder";
    }
}
