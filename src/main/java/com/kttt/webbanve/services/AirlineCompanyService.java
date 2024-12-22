package com.kttt.webbanve.services;

import com.kttt.webbanve.exception.AirlineCompanyNotFoundException;
import com.kttt.webbanve.models.AirlineCompany;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface AirlineCompanyService {
    Page<AirlineCompany> listByPage(int pageNum, String sortField, String sortDir, String keyword);
    List<AirlineCompany> findAll();
    AirlineCompany save(AirlineCompany airlineCompany);
    Optional<AirlineCompany> findById(int id);
    void deleteById(int id) throws AirlineCompanyNotFoundException;
    public AirlineCompany get(int id) throws AirlineCompanyNotFoundException;
}
