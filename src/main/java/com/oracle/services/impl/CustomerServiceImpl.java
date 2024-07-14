package com.oracle.services.impl;

import com.oracle.Entity.Account;
import com.oracle.Entity.Customer;
import com.oracle.repository.impl.JdbcCustomerRepository;
import com.oracle.services.CustomerService;
import com.oracle.repository.CustomerRepository;

import java.util.Date;
import java.util.Optional;

public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerServiceImpl() {
        this.customerRepository = new JdbcCustomerRepository();
    }

    @Override
    public Optional<Customer> createCustomer(String firstName, String lastName, String email, String phone, String address, Date dob, String password) {
        if (checkCustomerExists(email, phone)) {
            return Optional.empty();
        }


        Customer customer = new Customer(firstName, lastName, email, phone,address, dob, password);
        return Optional.of(customerRepository.save(customer));
    }

    @Override
    public boolean checkCustomerExists(String email, String phone) {
        return customerRepository.existsByEmailOrPhone(email, phone);
    }

    @Override
    public Customer getCustomerById(Long id){
        return customerRepository.getCustomerByCustomerId(id);
    }

    @Override
    public boolean verifyCustomerPassword(Long customerId, String password) {
        Customer customer = getCustomerById(customerId);
        if (customer != null) {
            // In a real-world application, you should use a proper password hashing mechanism
            return customer.getPassword().equals(password);
        }
        return false;
    }

    @Override
    public Optional<Customer> CustomerLogin(String email, String password) {
        return customerRepository.customerLogin(email,password);
    }




}