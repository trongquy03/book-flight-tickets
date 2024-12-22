package com.kttt.webbanve;


import com.kttt.webbanve.models.Ticket;
import com.kttt.webbanve.repositories.OrderRepositories;
import com.kttt.webbanve.services.GeneratePdfService;
import com.kttt.webbanve.services.MailSenderService;
import com.kttt.webbanve.services.SeatManagerService;
import com.kttt.webbanve.services.TicketService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

@SpringBootTest
class WebBanVeApplicationTests {
	@Autowired
	GeneratePdfService generatePdfService;

	@Autowired
	MailSenderService mailSenderService;

	@Autowired
	TicketService ticketService;
	@Autowired
	SeatManagerService seatManagerService;

	@Test
	void contextLoads() throws Exception {
//		Calendar calendar = Calendar.getInstance();
//		Date now = calendar.getTime();
//		System.out.println(now);
//		calendar.setTime(TimeUtil.stringToDate("2024-01-04"));
//		calendar.add(Calendar.MINUTE,15);
//		Date landing = calendar.getTime();
//		System.out.println(landing);
//		System.out.println(now.compareTo(landing));
		try {
			ArrayList<Ticket> ticketsDone = ticketService.getTicketsWaiting();
			for (Ticket t:ticketsDone){
				System.out.println(t);
			}
			System.out.println(ticketsDone.size());
//			seatManagerService.updateSeat(ticketsDone);
		} catch (ParseException e) {
			System.out.println(e.getMessage());
		}
	}
}
