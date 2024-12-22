package com.kttt.webbanve.repositories;

import com.kttt.webbanve.models.SeatCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SeatCategoryRepositories extends JpaRepository<SeatCategory,Integer> {
    public SeatCategory getSeatCategoryByCategoryName(String categoryName);
}
