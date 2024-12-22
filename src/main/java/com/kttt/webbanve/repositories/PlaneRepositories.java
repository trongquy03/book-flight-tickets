package com.kttt.webbanve.repositories;

import com.kttt.webbanve.models.Plane;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaneRepositories extends JpaRepository<Plane,Integer> {
    public Plane getPlaneByPlaneID(int id);
}
