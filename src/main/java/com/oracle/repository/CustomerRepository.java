package com.oracle.repository;

import com.oracle.Entity.Account;
import com.oracle.Entity.Customer;

import java.util.Optional;

public interface CustomerRepository {
    Customer save(Customer customer);
    boolean existsByEmailOrPhone(String email, String phone);
    Customer getCustomerByCustomerId(Long id);
    boolean verifyCustomerPassword(Long customerId, String password);
    Optional<Customer> customerLogin(String email, String password);

}