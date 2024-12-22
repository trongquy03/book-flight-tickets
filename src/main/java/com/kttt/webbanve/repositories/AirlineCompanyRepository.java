package com.kttt.webbanve.repositories;

import com.kttt.webbanve.models.AirlineCompany;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface AirlineCompanyRepository extends JpaRepository<AirlineCompany, Integer> {


}
