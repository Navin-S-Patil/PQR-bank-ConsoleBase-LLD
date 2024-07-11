package com.oracle.repository;

import com.oracle.Entity.Account;
import com.oracle.Entity.Customer;


public interface AccountRepository {
    Account save(Account account);
    Account getAccountByAccountNo(String accountNo);
}
