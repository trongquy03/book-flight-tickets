package com.kttt.webbanve.services;

import com.kttt.webbanve.models.Customer;
import org.springframework.stereotype.Service;

@Service
public interface CustomerService {
    public Customer getCustomer(String username);
    public Customer getCustomerByEmail(String email);
    public void updateCustomer(Customer newCus,Integer cid);
    public void createCustomer(Customer customer);
}
