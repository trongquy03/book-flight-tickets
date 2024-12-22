package com.kttt.webbanve.repositories;

import com.kttt.webbanve.models.PlaneFlight;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.ArrayList;

public interface PlaneFlightRepositories extends PagingAndSortingRepository<PlaneFlight,Integer> {
    Page<PlaneFlight> findAllByFlightID(int fid, Pageable pageable);
    ArrayList<PlaneFlight> findPlaneFlightsByPlaneID(int planeID);
}
