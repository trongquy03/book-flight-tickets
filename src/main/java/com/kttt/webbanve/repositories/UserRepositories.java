package com.kttt.webbanve.repositories;

import com.kttt.webbanve.models.Customer;
import com.kttt.webbanve.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepositories extends JpaRepository<User,Integer> {
//    @Query(value = "select * from Customer cus where cus.getUser().username = :username",nativeQuery = true)
//    public Customer getCustomerByUsername(@Param("username") String username);
    public User findByUsername(String username);
}
