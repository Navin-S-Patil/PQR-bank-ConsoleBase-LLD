package com.oracle.controller;

import com.oracle.Entity.Account;
import com.oracle.Entity.Customer;
import com.oracle.Entity.Transaction;
import com.oracle.services.AccountService;
import com.oracle.services.CustomerService;

import java.util.List;
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

    public Optional<List<Account>> getBalance(Customer customer){
        return accountService.getBalance(customer);
    }

    public boolean deposit(String accountNumber, double amount, String description) {
        return accountService.deposit(accountNumber, amount, description);
    }

    public List<Transaction> getTransactionHistory(String accountNumber) {
        return accountService.getTransactionHistory(accountNumber);
    }

    public Optional<List<Account>> getAccounts(Customer customer){
        return accountService.getAccounts(customer);
    }

    public boolean withdraw(String accountNumber, double amount, String description){
        return accountService.withdraw(accountNumber,amount,description);
    }

    public boolean transfer(String accountNumber, double amount, String beneficiaryAccountNumber, String beneficiaryAccountType, String description){
        return accountService.transfer(accountNumber,amount,beneficiaryAccountNumber,beneficiaryAccountType,description);
    }
}