package com.kttt.webbanve.services;

import com.kttt.webbanve.TimeUtil;
import com.kttt.webbanve.models.Flight;
import com.kttt.webbanve.models.Seat;
import com.kttt.webbanve.models.Ticket;
import com.kttt.webbanve.repositories.SeatRepositories;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
@Slf4j
@Service
public class SeatManagerServiceImpl implements SeatManagerService{
    @Autowired
    SeatRepositories seatRepositories;
    @Autowired
    FlightService flightService;
    @Override
    public ArrayList<Seat> getAllSeats() {
        return seatRepositories.getAllSeats();
    }

    @Transactional
    @Override
    public synchronized void updateSeat(ArrayList<Ticket> ticketsWaiting) {
        ArrayList<Seat> seatsBusy = new ArrayList<>();
        for(Ticket t : ticketsWaiting){
            seatsBusy.add(t.getSeat());
        }
        ArrayList<Seat> seats = seatRepositories.getAllSeats();
        for (Seat seat : seats) {
            int check = 0;
            for (Seat seatWait : seatsBusy){
                if(seat.getSeatID() == seatWait.getSeatID()){
                    check++;
                }
            }
            if(check == 0){
                seat.setStatus(0);
                seatRepositories.save(seat);
            }
        }
    }
}
