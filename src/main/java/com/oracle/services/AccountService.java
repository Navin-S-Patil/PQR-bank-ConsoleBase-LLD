package com.oracle.services;

import com.oracle.Entity.Account;
import com.oracle.Entity.Customer;
import com.oracle.Entity.Transaction;

import java.util.List;
import java.util.Optional;

public interface AccountService {
    Optional<Account> createAccount(Long customerId, String accountType);
    Account getAccountByAccountNumber(String accountNumber);
    Optional<List<Account>> getBalance(Customer customer);
    Optional<List<Account>> getAccounts(Customer customer);
    boolean deposit(String accountNumber, double amount, String description);
    List<Transaction> getTransactionHistory(String accountNumber);
    public boolean withdraw(String accountNumber, double amount, String description);

    boolean transfer(String accountNumber, double amount, String beneficiaryAccountNumber, String beneficiaryAccountType, String description);
}