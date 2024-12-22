package com.kttt.webbanve.Configs;

import com.kttt.webbanve.models.Seat;
import com.kttt.webbanve.models.Ticket;
import com.kttt.webbanve.services.SeatManagerService;
import com.kttt.webbanve.services.TicketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
@Component
public class updateSeatTaskTimer extends TimerTask {
    final static Logger logger = LoggerFactory.getLogger(updateSeatTaskTimer.class);
    @Autowired
    SeatManagerService seatManagerService;
    @Autowired
    TicketService ticketService;
    @Scheduled(fixedDelay = 10000L)
    @Override
    public void run() { // Cập nhật trang thái ghế
        try {
            ArrayList<Ticket> ticketsWaiting = ticketService.getTicketsWaiting();
            seatManagerService.updateSeat(ticketsWaiting);
        } catch (ParseException e) {
            logger.error(e.getMessage(),e.getCause());
        }
    }
}
