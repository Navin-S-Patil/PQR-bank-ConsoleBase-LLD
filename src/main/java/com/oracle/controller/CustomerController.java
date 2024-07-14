package com.oracle.controller;

import com.oracle.Entity.Account;
import com.oracle.Entity.Customer;
import com.oracle.services.CustomerService;

import java.util.Date;
import java.util.Optional;

public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    public void createCustomer(String firstName, String lastName, String email, String phone, String address, Date dob, String password) {
        Optional<Customer> createdCustomer = customerService.createCustomer(firstName, lastName, email, phone, address, dob, password);

        if (createdCustomer.isPresent()) {
            System.out.println("Customer created successfully: " + createdCustomer.get());
        } else {
            System.out.println("Customer already exists");
        }
    }

    public Optional<Customer> customerLogin(String email, String password){
        return customerService.CustomerLogin(email,password);
    }


}