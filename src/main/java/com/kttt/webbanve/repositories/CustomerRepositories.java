package com.kttt.webbanve.repositories;

import com.kttt.webbanve.models.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepositories extends JpaRepository<Customer,Integer> {
    public Customer findCustomerByUser_Username(String username);
    public Customer findCustomerByCustomerID(int id);
    public Customer getCustomerByEmail(String email);
    public Customer getCustomerByCitizenIdentification(String cid);
}
