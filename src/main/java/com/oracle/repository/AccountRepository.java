package com.oracle.repository;

import com.oracle.Entity.Account;
import com.oracle.Entity.Customer;
import com.oracle.Entity.Transaction;

import java.util.List;
import java.util.Optional;


public interface AccountRepository {
    Account save(Account account);
    Account getAccountByAccountNo(String accountNo);
    Optional<List<Account>> getBalance(Customer customer);
    Optional<List<Account>> getAccounts(Customer customer);
    boolean deposit(String accountNumber, double amount, String description);
    List<Transaction> getTransactionHistory(String accountNumber);
    boolean withdraw(String accountNumber, double amount, String description);

    boolean transfer(String accountNumber, double amount, String beneficiaryAccountNumber, String beneficiaryAccountType, String description);
}
