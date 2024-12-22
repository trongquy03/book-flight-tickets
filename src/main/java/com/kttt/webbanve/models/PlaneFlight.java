package com.kttt.webbanve.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "plane_flight")
public class PlaneFlight {

    @JoinColumn(name = "flightID")
    private int flightID;

    @Id
    @JoinColumn(name = "planeID")
    private int planeID;

}
