package com.kttt.webbanve.models;

import jakarta.persistence.*;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Entity
@Table(name = "flight")
public class Flight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "flightID")
    private int flightID;

    @Column(name = "departingFrom")
    private String departingFrom;

    @Column(name = "arrivingAt")
    private String arrivingAt;

    @Column(name = "flightTime")
    private String flightTime;

    @Column(name = "departureTime")
    private String departureTime;

    @Column(name = "dateFlight")
    private String dateFlight;

    @Column(name = "feeFlight")
    private long feeFlight;

    @Column(name = "travelTime")
    private String travelTime;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "plane_flight", joinColumns = {@JoinColumn(name = "flightID",referencedColumnName = "flightID")}, inverseJoinColumns = {@JoinColumn(name = "planeID",referencedColumnName = "planeID")})
    private List<Plane> planes = new ArrayList<>();

    public Flight(){}

    public int getFlightID() {
        return flightID;
    }

    public void setFlightID(int flightID) {
        this.flightID = flightID;
    }

    public String getDepartingFrom() {
        return departingFrom;
    }

    public void setDepartingFrom(String departingFrom) {
        this.departingFrom = departingFrom;
    }

    public String getArrivingAt() {
        return arrivingAt;
    }

    public void setArrivingAt(String arrivingAt) {
        this.arrivingAt = arrivingAt;
    }

    public String getFlightTime() {
        return flightTime;
    }

    public void setFlightTime(String flightTime) {
        this.flightTime = flightTime;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public String getDateFlight() {
        return dateFlight;
    }

    public void setDateFlight(String dateFlight) {
        this.dateFlight = dateFlight;
    }

    public long getFeeFlight() {
        return feeFlight;
    }

    public void setFeeFlight(long feeFlight) {
        this.feeFlight = feeFlight;
    }

    public String getTravelTime() {
        return travelTime;
    }

    public void setTravelTime(String travelTime) {
        this.travelTime = travelTime;
    }

    public List<Plane> getPlanes() {
        return planes;
    }

    public void setPlanes(List<Plane> planes) {
        this.planes = planes;
    }

    public String getFormatFeeFlight(long feeFlight) {
        Locale lc = new Locale("nv", "VN");
        NumberFormat nf = NumberFormat.getInstance(lc);
        return nf.format(feeFlight) + " vnđ";
    }

    public String getFormatTime(String flightTime) {
        for(int i=0; i<flightTime.length(); i++) {
            if(flightTime.charAt(0) == '0') {
                flightTime=flightTime.substring(1);
            } else {
                break;
            }
        }
        if(Integer.parseInt(flightTime)<24) {
            return flightTime + ":00";
        }
        else if (Integer.parseInt(flightTime)>=24) {
            int day = Integer.parseInt(flightTime) / 24;
            int hour = Integer.parseInt(flightTime) - day * 24;
            return day+"d " + hour +"h";
        }
        return flightTime;
    }

    public String floatToInt(Float fee) {
        return String.valueOf(fee.intValue());
    }

    public String getFlightRouter() {
        return this.getDepartingFrom() + " -> " + this.getArrivingAt();
    }
}
