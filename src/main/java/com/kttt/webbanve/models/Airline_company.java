package com.kttt.webbanve.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "airline_company")
public class Airline_company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "airlineID")
    private int airlineID;

    @Column(name = "airline_name")
    private String airline_name;

    @Column(name = "airline_image")
    private String airline_image;

    @Column(name = "airline_fee")
    private long airline_fee;

}
