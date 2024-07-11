package com.oracle.services;

import com.oracle.Entity.Account;

import java.util.Optional;

public interface AccountService {
    Optional<Account> createAccount(Long customerId, String accountType);
    Account getAccountByAccountNumber(String accountNumber);
}