package com.oracle.controller;

import com.oracle.Entity.Account;
import com.oracle.services.AccountService;
import com.oracle.services.CustomerService;

import java.util.Optional;

public class AccountController {
    private final AccountService accountService;
    private final CustomerService customerService;

    public AccountController(AccountService accountService, CustomerService customerService) {
        this.accountService = accountService;
        this.customerService = customerService;
    }

    public void createAccount(Long customerId, String accountType, String password) {
        if (!customerService.verifyCustomerPassword(customerId, password)) {
            System.out.println("Invalid customer ID or password");
            return;
        }

        Optional<Account> createdAccount = accountService.createAccount(customerId, accountType);

        if (createdAccount.isPresent()) {
            System.out.println("Account created successfully: " + createdAccount.get().getAccountNo());
        } else {
            System.out.println("Failed to create account");
        }
    }
}