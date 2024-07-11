package com.oracle.services.impl;

import com.oracle.Entity.Account;
import com.oracle.repository.AccountRepository;
import com.oracle.repository.impl.JdbcAccountRepository;
import com.oracle.services.AccountService;

import java.util.Optional;

public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;

    public AccountServiceImpl() {
        this.accountRepository = new JdbcAccountRepository();
    }

    @Override
    public Optional<Account> createAccount(Long customerId, String accountType) {
        Account account = new Account(customerId, accountType, "ACTIVE");
        Account savedAccount = accountRepository.save(account);
        return Optional.ofNullable(savedAccount);
    }

    @Override
    public Account getAccountByAccountNumber(String accountNumber) {
        return accountRepository.getAccountByAccountNo(accountNumber);
    }
}