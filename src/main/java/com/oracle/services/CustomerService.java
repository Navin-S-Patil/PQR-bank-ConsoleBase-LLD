package com.oracle.services;

import com.oracle.Entity.Account;
import com.oracle.Entity.Customer;
import java.util.Date;
import java.util.Optional;

public interface CustomerService {
    Optional<Customer> createCustomer(String firstName, String lastName, String email, String phone, String address, Date dob, String password);
    boolean checkCustomerExists(String email, String phone);
    Customer getCustomerById(Long id);
    boolean verifyCustomerPassword(Long customerId, String password);
    Optional<Customer> CustomerLogin(String email, String password);

}