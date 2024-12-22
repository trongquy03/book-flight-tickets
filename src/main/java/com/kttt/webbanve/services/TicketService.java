package com.kttt.webbanve.services;

import com.kttt.webbanve.models.Ticket;
import com.kttt.webbanve.payload.CountOrderOfQuantityTicket;
import com.kttt.webbanve.payload.TicketStatistics;
import com.kttt.webbanve.payload.TicketStatisticsByQuarter;
import com.kttt.webbanve.payload.UniqueYear;
import org.springframework.data.domain.Page;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public interface TicketService {
    Page<Ticket> findAll(int pageNo, int pageSize, String sortBy, String sortDir);
    List<TicketStatistics> countTicket();
    List<TicketStatisticsByQuarter> countTicketByQuarter();
    List<CountOrderOfQuantityTicket> ticketPerOrder();
    List<Integer> getUniqueYear();
    List<Integer> getNumberYearsFrom(int year, int numberOfYear);
    ArrayList<Ticket> getTicketsByOrderID(int oid);
    ArrayList<Ticket> getTicketsWaiting() throws ParseException;
}
