package com.kttt.webbanve.repositories;

import com.kttt.webbanve.models.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FlightRepository extends JpaRepository<Flight, Integer> {

    @Query("SELECT f FROM Flight f WHERE " +
            "f.departingFrom LIKE CONCAT('%',:query, '%') " +
            "Or f.arrivingAt LIKE CONCAT('%',:query, '%')")
    List<Flight> searchFlights(String query);
}
