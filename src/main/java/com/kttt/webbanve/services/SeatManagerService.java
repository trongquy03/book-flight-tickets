package com.kttt.webbanve.services;

import com.kttt.webbanve.models.Seat;
import com.kttt.webbanve.models.Ticket;
import com.kttt.webbanve.repositories.SeatRepositories;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.ArrayList;

@Service
public interface SeatManagerService {
    public ArrayList<Seat> getAllSeats();

    public void updateSeat(ArrayList<Ticket> ticketsWaiting) throws ParseException;
}
