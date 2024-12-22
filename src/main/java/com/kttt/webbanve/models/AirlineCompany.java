package com.kttt.webbanve.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "airline_company")
public class AirlineCompany {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "airlineID")
    private int airlineID;

    @Column(name = "airline_name")
    private String airlineName;

    @Column(name = "airline_image")
    private String airlineLogo;

    @Column(name = "airline_fee")
    private long airline_fee;

    public int getAirlineID() {
        return airlineID;
    }


    @Transient
    public String getPhotosImagePath() {
        if (airlineLogo == null) return "/images/default-image.png";

        return "/admin/airlineCompany-photos/" + this.airlineID + "/" + this.airlineLogo;
    }
}
