package com.oracle.Entity;

import java.util.Date;

public class Transaction {
    private Long id;
    private String accountNo;
    private TransactionType transactionType;
    private double amount;
    private Date transactionDate;
    private String description;
    private String status;

    public enum TransactionType {
        DEPOSIT, WITHDRAWAL, TRANSFER
    }

    // Constructor
    public Transaction(String accountNo, TransactionType transactionType, double amount, String description, String status) {
        this.accountNo = accountNo;
        this.transactionType = transactionType;
        this.amount = amount;
        this.description = description;
        this.transactionDate = new Date();
        this.status = status;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}