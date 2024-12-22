package com.kttt.webbanve.models.supportModels;

import com.kttt.webbanve.models.*;

import java.util.ArrayList;

public class FlightSelected {
    private Luggage luggage;
    private Customer customer;
    private Seat seat;
    private Flight flight;
    private long airfares;

    private ArrayList<Seat> seats;

    public void setAirfares(long airfares) {
        this.airfares = airfares;
    }

    public long getAirfares() {
        return airfares;
    }

    public void setSeats(ArrayList<Seat> seats) {
        this.seats = seats;
    }

    public ArrayList<Seat> getSeats() {
        return seats;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }
    public Flight getFlight() {
        return flight;
    }

    public void setSeat(Seat seat) {
        this.seat = seat;
    }

    public Seat getSeat() {
        return seat;
    }

    public Luggage getLuggage() {
        return luggage;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setLuggage(Luggage luggage) {
        this.luggage = luggage;
    }
}
