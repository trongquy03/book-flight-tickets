package com.kttt.webbanve.services;

import com.kttt.webbanve.models.Flight;
import com.kttt.webbanve.models.PlaneFlight;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public interface PlaneFlightService {
    Page<PlaneFlight> getAllFlight(int fid,int pageNum);
}
