package com.kttt.webbanve.repositories;

import com.kttt.webbanve.models.Flight;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.ArrayList;

public interface FlightRepositories extends PagingAndSortingRepository<Flight, Integer> {
    public Page<Flight> findAllByDateFlightAndDepartingFromAndArrivingAt(String date_flight,String departing_from,String arriving_at, Pageable pageable);

    public Flight getFlightByFlightID(int fid);

    @Query(value = "select distinct f from Flight f join f.planes p where f.flightID = :fid and p.planeID = :pid")
    public Flight getFlightByIDAndPlaneID(@Param("fid") int fid,@Param("pid") int pid);
}
