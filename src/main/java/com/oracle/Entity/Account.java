package com.oracle.Entity;

import java.util.Date;
import java.util.Random;

public class Account {
    private final String accountNo;
    private final Long customerId;
    private String accountType;
    private double balance;
    private final Date openingDate;
    private String status;

    public Account(Long customerId, String accountType, String status) {
        this.accountNo = generateAccountNumber();
        this.customerId = customerId;
        this.accountType = accountType;
        this.status = status;
        this.balance = 0;
        this.openingDate = new Date();
    }

    public Account(String accountNo, Long customerId, String accountType, double balance, Date openingDate, String status) {
        this.accountNo = accountNo;
        this.customerId = customerId;
        this.accountType = accountType;
        this.balance = balance;
        this.openingDate = openingDate;
        this.status = status;
    }

    private String generateAccountNumber() {
        Random random = new Random();
        StringBuilder accountNumber = new StringBuilder();

        for (int i = 0; i < 12; i++) {
            accountNumber.append(random.nextInt(10));
        }

        return accountNumber.toString();
    }

    public String getAccountType() {
        return accountType;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public Date getOpeningDate() {
        return openingDate;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }



    @Override
    public String toString() {
        return "Account{" +
                "accountNo='" + accountNo + '\'' +
                ", customerId=" + customerId +
                ", accountType='" + accountType + '\'' +
                ", balance=" + balance +
                ", openingDate=" + openingDate +
                ", status='" + status + '\'' +
                '}';
    }
}