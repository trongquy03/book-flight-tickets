package com.kttt.webbanve.services;

import com.kttt.webbanve.TimeUtil;
import com.kttt.webbanve.models.Ticket;
import com.kttt.webbanve.payload.*;
import com.kttt.webbanve.repositories.StatisticsRepoCustomImpl;
import com.kttt.webbanve.repositories.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class TicketServiceImpl implements TicketService{
    private TicketRepository ticketRepository;
    private StatisticsRepoCustomImpl srci;

    @Autowired
    public TicketServiceImpl(TicketRepository ticketRepository, StatisticsRepoCustomImpl srci) {
        this.ticketRepository = ticketRepository;
        this.srci = srci;
    }


    @Override
    public Page<Ticket> findAll(int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        Page<Ticket> tickets = ticketRepository.findAll(pageable);
        return tickets;
    }

    @Override
    public List<TicketStatistics> countTicket() {
        return srci.statisticTicketByMonth();
    }

    @Override
    public List<TicketStatisticsByQuarter> countTicketByQuarter() {
        return srci.statisticTicketByQuarter();
    }

    @Override
    public List<CountOrderOfQuantityTicket> ticketPerOrder() {
        return srci.statisticsTicketPerOrder();
    }

    @Override
    public List<Integer> getUniqueYear() {
        return srci.getUniqueYear();
    }

    @Override
    public List<Integer> getNumberYearsFrom(int year, int numberOfYear) {
        return srci.getNumberYearsFrom(year, numberOfYear);
    }

    @Override
    public ArrayList<Ticket> getTicketsByOrderID(int oid) {
        return ticketRepository.getTicketsByOrder_OrderID(oid);
    }

    @Override
    public ArrayList<Ticket> getTicketsWaiting() throws ParseException {
        Date now = Calendar.getInstance().getTime();
        ArrayList<Ticket> tickets = (ArrayList<Ticket>) ticketRepository.findAll();
        ArrayList<Ticket> ticketsWaiting = new ArrayList<>();
        for(Ticket t : tickets){
            if(TimeUtil.stringToDate(t.getFlight().getDateFlight()).compareTo(now) > 0)
                ticketsWaiting.add(t);
        }
        return ticketsWaiting;
    }


//    private TicketDto mapToDto(Ticket ticket) {
//        TicketDto ticketDto = new TicketDto();
//        ticketDto.setTicketId(ticket.getTicketId());
//        ticketDto.setAirfares(ticket.getAirfares());
//        ticketDto.setLuggageId(ticket.getLuggageId());
//        ticketDto.setFlightId(ticket.getFlightId());
//        return ticketDto;
//    }
}
