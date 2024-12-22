package com.kttt.webbanve.repositories;

import com.kttt.webbanve.models.AirlineCompany;
import com.kttt.webbanve.models.Plane;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PlaneRepository extends JpaRepository<Plane, Integer> {
    Plane findByPlaneID(int pid);
}
