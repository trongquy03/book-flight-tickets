package com.kttt.webbanve.repositories;

import com.kttt.webbanve.models.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;

public interface TicketRepository extends JpaRepository<Ticket, Integer> {
    ArrayList<Ticket> getTicketsByOrder_OrderID(int oid);
}
