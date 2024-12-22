package com.kttt.webbanve.services;

import com.kttt.webbanve.models.Flight;
import com.kttt.webbanve.models.PlaneFlight;
import com.kttt.webbanve.repositories.FlightRepositories;
import com.kttt.webbanve.repositories.FlightRepository;
import com.kttt.webbanve.repositories.PlaneFlightRepositories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FlightServiceImpl implements FlightService{
    @Autowired
    FlightRepositories fl;
    @Autowired
    private FlightRepository flightRepository;
    @Autowired
    PlaneFlightRepositories planeFlightRepositories;
    @Autowired
    public FlightServiceImpl(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    @Override
    public Page<Flight> findAll(int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        Page<Flight> flights = flightRepository.findAll(pageable);
        return flights;
    }

    @Override
    public Flight save(Flight flight) {
        return flightRepository.save(flight);
    }

    @Override
    public Optional<Flight> findById(int id) {
        return flightRepository.findById(id);
    }

    @Override
    public void deleteById(int id) {
        flightRepository.deleteById(id);
    }

    @Override
    public List<Flight> searchFlights(String query) {
        return flightRepository.searchFlights(query);
    }

    public List<Flight> getAllFlights(){
        return flightRepository.findAll();
    }

    @Override
    public List<Flight> findFlightByForm() {
        return null;
    }

    public Page<Flight> findFlightByForm(String date_flight,String departing_from,String arriving_at,int pageNumber){
        Pageable pageable = PageRequest.of(pageNumber-1,4);
        return fl.findAllByDateFlightAndDepartingFromAndArrivingAt(date_flight,departing_from,arriving_at,pageable);
    }

    public Flight getFlightByID(int fid){
        return fl.getFlightByFlightID(fid);
    }

    @Override
    public ArrayList<Flight> getFlightsByPlane(int planeID) {
        ArrayList< PlaneFlight> planeFlights = planeFlightRepositories.findPlaneFlightsByPlaneID(planeID);
        ArrayList<Flight> flights = new ArrayList<>();
        for(PlaneFlight pl : planeFlights){
            flights.add(fl.getFlightByFlightID(pl.getFlightID()));
        }
        return flights;
    }
}
