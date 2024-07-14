package com.oracle.services.impl;

import com.oracle.Entity.Account;
import com.oracle.Entity.Customer;
import com.oracle.Entity.Transaction;
import com.oracle.repository.AccountRepository;
import com.oracle.repository.impl.JdbcAccountRepository;
import com.oracle.services.AccountService;

import java.util.List;
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

    @Override
    public Optional<List<Account>> getBalance(Customer customer) {
        return accountRepository.getBalance(customer);
    }

    @Override
    public boolean deposit(String accountNumber, double amount, String description) {
        if (amount <= 0) {
            System.out.println("Deposit amount must be positive.");
            return false;
        }
        return accountRepository.deposit(accountNumber, amount, description);
    }

    @Override
    public List<Transaction> getTransactionHistory(String accountNumber) {
        return accountRepository.getTransactionHistory(accountNumber);
    }

    @Override
    public Optional<List<Account>> getAccounts(Customer customer){
        return accountRepository.getAccounts(customer);
    }

    @Override
    public boolean withdraw(String accountNumber, double amount, String description){
        return accountRepository.withdraw(accountNumber,amount,description);
    }

    @Override
    public boolean transfer(String accountNumber, double amount, String beneficiaryAccountNumber, String beneficiaryAccountType, String description) {
        return accountRepository.transfer(accountNumber,amount,beneficiaryAccountNumber,beneficiaryAccountType,description);
    }
}