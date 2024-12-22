package com.kttt.webbanve.services;

import com.kttt.webbanve.models.Flight;
import com.kttt.webbanve.models.Plane;
import com.kttt.webbanve.models.PlaneFlight;
import com.kttt.webbanve.repositories.FlightRepositories;
import com.kttt.webbanve.repositories.PlaneFlightRepositories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PlaneFlightServiceImpl implements PlaneFlightService{
    @Autowired
    PlaneFlightRepositories pfr;

    @Autowired
    FlightServiceImpl flightService;

    @Autowired
    PlaneServiceImpl planeService;
    @Override
    public Page<PlaneFlight> getAllFlight(int fid,int pageNum) {
        Pageable pageable = PageRequest.of(pageNum-1,4);
        return pfr.findAllByFlightID(fid,pageable);
    }

    public ArrayList<Plane> convertToListFlight(List<PlaneFlight> pfs) {
        ArrayList<Plane> ps = new ArrayList<>();
        for(PlaneFlight planeFlight : pfs){
            Flight f = flightService.getFlightByID(planeFlight.getFlightID());
            Plane p = planeService.findByPID(planeFlight.getPlaneID());
            ArrayList<Flight> flights = new ArrayList<>();
            flights.add(f);
            p.setFlights(flights);
            ps.add(p);
        }
        return ps;
    }
}
