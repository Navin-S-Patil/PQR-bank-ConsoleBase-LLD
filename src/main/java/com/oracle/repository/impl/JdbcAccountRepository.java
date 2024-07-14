package com.oracle.repository.impl;

import com.oracle.Entity.Account;
import com.oracle.Entity.Customer;
import com.oracle.Entity.Transaction;
import com.oracle.repository.AccountRepository;
import com.oracle.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcAccountRepository implements AccountRepository {

    @Override
    public Account save(Account account) {
        String sql = "INSERT INTO accounts (account_no, customer_id, account_type, balance, opening_date, status) VALUES (?, ?, ?, ?, ?, ?)";

        return DatabaseConnection.executeWithConnection(conn -> {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, account.getAccountNo());
                pstmt.setLong(2, account.getCustomerId());
                pstmt.setString(3, account.getAccountType());
                pstmt.setDouble(4, account.getBalance());
                pstmt.setDate(5, new java.sql.Date(account.getOpeningDate().getTime()));
                pstmt.setString(6, account.getStatus());

                int affectedRows = pstmt.executeUpdate();

                if (affectedRows == 0) {
                    throw new SQLException("Creating account failed, no rows affected.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
            return account;
        });
    }

    @Override
    public Account getAccountByAccountNo(String accountNumber) {
        String sql = "SELECT * FROM accounts WHERE account_no = ?";
        return DatabaseConnection.executeWithConnection(conn -> {
            Account account = null;
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, accountNumber);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        account = new Account(
                                rs.getString("account_no"),
                                rs.getLong("customer_id"),
                                rs.getString("account_type"),
                                rs.getDouble("balance"),
                                rs.getDate("opening_date"),
                                rs.getString("status")
                        );
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return account;
        });
    }

    @Override
    public Optional<List<Account>> getBalance(Customer customer) {
        String sql = "SELECT * FROM customers c RIGHT JOIN accounts a ON c.id = a.customer_id WHERE c.id = ? AND c.email = ?";
        return DatabaseConnection.executeWithConnection(connection -> {
            List<Account> accounts = new ArrayList<>();
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setLong(1, customer.getId());
                pstmt.setString(2, customer.getEmail());
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Account account = new Account(
                                rs.getString("account_no"),
                                rs.getLong("customer_id"),
                                rs.getString("account_type"),
                                rs.getDouble("balance"),
                                rs.getDate("opening_date"),
                                rs.getString("status")
                        );
                        accounts.add(account);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return Optional.empty();
            }
            return accounts.isEmpty() ? Optional.empty() : Optional.of(accounts);
        });
    }

    @Override
    public Optional<List<Account>> getAccounts(Customer customer){
        String sql = "SELECT * FROM customers c RIGHT JOIN accounts a ON c.id = a.customer_id WHERE c.id = ? AND c.email = ?";
        return DatabaseConnection.executeWithConnection(connection -> {
            List<Account> accounts = new ArrayList<>();
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setLong(1, customer.getId());
                pstmt.setString(2, customer.getEmail());
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Account account = new Account(
                                rs.getString("account_no"),
                                rs.getLong("customer_id"),
                                rs.getString("account_type"),
                                rs.getDouble("balance"),
                                rs.getDate("opening_date"),
                                rs.getString("status")
                        );
                        accounts.add(account);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return Optional.empty();
            }
            return accounts.isEmpty() ? Optional.empty() : Optional.of(accounts);
        });
    }

    @Override
    public List<Transaction> getTransactionHistory(String accountNumber) {
        String sql = "SELECT * FROM transactions WHERE account_no = ? ORDER BY transaction_date DESC";
        return DatabaseConnection.executeWithConnection(conn -> {
            List<Transaction> transactions = new ArrayList<>();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, accountNumber);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Transaction transaction = new Transaction(
                                rs.getString("account_no"),
                                Transaction.TransactionType.valueOf(rs.getString("transaction_type")),
                                rs.getDouble("amount"),
                                rs.getString("description"),
                                rs.getString("status")
                        );
                        transaction.setId(rs.getLong("id"));
                        transaction.setTransactionDate(rs.getTimestamp("transaction_date"));
                        transactions.add(transaction);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return transactions;
        });
    }

    @Override
    public boolean deposit(String accountNumber, double amount, String description) {
        String updateBalanceSql = "UPDATE accounts SET balance = balance + ? WHERE account_no = ?";
        String insertTransactionSql = "INSERT INTO transactions (account_no, transaction_type, amount, description, status) VALUES (?, ?, ?, ?, ?)";

        return Boolean.TRUE.equals(DatabaseConnection.executeWithConnection(conn -> {
            boolean success = false;
            try {
                conn.setAutoCommit(false);
                // Update balance
                try (PreparedStatement pstmt = conn.prepareStatement(updateBalanceSql)) {
                    pstmt.setDouble(1, amount);
                    pstmt.setString(2, accountNumber);
                    int affectedRows = pstmt.executeUpdate();
                    if (affectedRows == 0) {
                        throw new SQLException("Deposit failed, no account found.");
                    }
                }

                // Insert transaction record
                try (PreparedStatement pstmt = conn.prepareStatement(insertTransactionSql)) {
                    pstmt.setString(1, accountNumber);
                    pstmt.setString(2, Transaction.TransactionType.DEPOSIT.name());
                    pstmt.setDouble(3, amount);
                    pstmt.setString(4, description);
                    pstmt.setString(5, "SUCCESS");
                    pstmt.executeUpdate();
                }

                conn.commit();
                success = true;
                System.out.println("Deposit of $" + amount + " successful for account " + accountNumber);
            } catch (SQLException e) {
                try {
                    conn.rollback();
                    // Insert failed transaction record
                    try (PreparedStatement pstmt = conn.prepareStatement(insertTransactionSql)) {
                        pstmt.setString(1, accountNumber);
                        pstmt.setString(2, Transaction.TransactionType.DEPOSIT.name());
                        pstmt.setDouble(3, amount);
                        pstmt.setString(4, description + " (Failed: " + e.getMessage() + ")");
                        pstmt.setString(5, "FAIL");
                        pstmt.executeUpdate();
                    }
                    conn.commit();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                e.printStackTrace();
                System.out.println("Deposit failed. Error: " + e.getMessage());
            } finally {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            return success;
        }));
    }

    @Override
    public boolean withdraw(String accountNumber, double amount, String description) {
        String checkBalanceSql = "SELECT balance FROM accounts WHERE account_no = ?";
        String updateBalanceSql = "UPDATE accounts SET balance = balance - ? WHERE account_no = ?";
        String insertTransactionSql = "INSERT INTO transactions (account_no, transaction_type, amount, description, status) VALUES (?, ?, ?, ?, ?)";

        return Boolean.TRUE.equals(DatabaseConnection.executeWithConnection(conn -> {
            boolean success = false;
            try {
                conn.setAutoCommit(false);

                // Check if sufficient balance
                double currentBalance;
                try (PreparedStatement pstmt = conn.prepareStatement(checkBalanceSql)) {
                    pstmt.setString(1, accountNumber);
                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException("Account not found.");
                        }
                        currentBalance = rs.getDouble("balance");
                    }
                }

                if (currentBalance < amount) {
                    throw new SQLException("Insufficient funds.");
                }

                // Update balance
                try (PreparedStatement pstmt = conn.prepareStatement(updateBalanceSql)) {
                    pstmt.setDouble(1, amount);
                    pstmt.setString(2, accountNumber);
                    int affectedRows = pstmt.executeUpdate();
                    if (affectedRows == 0) {
                        throw new SQLException("Withdrawal failed, no account found.");
                    }
                }

                // Insert transaction record
                try (PreparedStatement pstmt = conn.prepareStatement(insertTransactionSql)) {
                    pstmt.setString(1, accountNumber);
                    pstmt.setString(2, Transaction.TransactionType.WITHDRAWAL.name());
                    pstmt.setDouble(3, amount);
                    pstmt.setString(4, description);
                    pstmt.setString(5, "SUCCESS");
                    pstmt.executeUpdate();
                }

                conn.commit();
                success = true;
                System.out.println("Withdrawal of $" + amount + " successful from account " + accountNumber);
            } catch (SQLException e) {
                try {
                    conn.rollback();
                    // Insert failed transaction record
                    try (PreparedStatement pstmt = conn.prepareStatement(insertTransactionSql)) {
                        pstmt.setString(1, accountNumber);
                        pstmt.setString(2, Transaction.TransactionType.WITHDRAWAL.name());
                        pstmt.setDouble(3, amount);
                        pstmt.setString(4, description + " (Failed: " + e.getMessage() + ")");
                        pstmt.setString(5, "FAIL");
                        pstmt.executeUpdate();
                    }
                    conn.commit();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                e.printStackTrace();
                System.out.println("Withdrawal failed. Error: " + e.getMessage());
            } finally {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            return success;
        }));
    }

    @Override
    public boolean transfer(String accountNumber, double amount, String beneficiaryAccountNumber, String beneficiaryAccountType, String description) {
        String checkBalanceSql = "SELECT balance FROM accounts WHERE account_no = ?";
        String updateBalanceSql = "UPDATE accounts SET balance = balance - ? WHERE account_no = ?";
        String updateBeneficiaryBalanceSql = "UPDATE accounts SET balance = balance + ? WHERE account_no = ?";
        String insertTransactionSql = "INSERT INTO transactions (account_no, transaction_type, amount, description, status) VALUES (?, ?, ?, ?, ?)";


        return Boolean.TRUE.equals(DatabaseConnection.executeWithConnection(conn -> {
            boolean success = false;
            try {
                conn.setAutoCommit(false);

                // Check if sufficient balance
                double currentBalance;
                try (PreparedStatement pstmt = conn.prepareStatement(checkBalanceSql)) {
                    pstmt.setString(1, accountNumber);
                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException("Account not found.");
                        }
                        currentBalance = rs.getDouble("balance");
                    }
                }

                if (currentBalance < amount) {
                    throw new SQLException("Insufficient funds.");
                }

                // Update balance
                try (PreparedStatement pstmt = conn.prepareStatement(updateBalanceSql)) {
                    pstmt.setDouble(1, amount);
                    pstmt.setString(2, accountNumber);
                    int affectedRows = pstmt.executeUpdate();
                    if (affectedRows == 0) {
                        throw new SQLException("Withdrawal failed, no account found.");
                    }
                }

                // Insert transaction record
                //user account
                try (PreparedStatement pstmt = conn.prepareStatement(insertTransactionSql)) {
                    pstmt.setString(1, accountNumber);
                    pstmt.setString(2, Transaction.TransactionType.WITHDRAWAL.name());
                    pstmt.setDouble(3, amount);
                    pstmt.setString(4,  "To beni Acc:"+ beneficiaryAccountNumber+ " Desc: " + description);
                    pstmt.setString(5, "SUCCESS");
                    pstmt.executeUpdate();
                }

                try(PreparedStatement pstmt = conn.prepareStatement(updateBeneficiaryBalanceSql)){
                    pstmt.setDouble(1,amount);
                    pstmt.setString(2,beneficiaryAccountNumber);
                    int affectedRows = pstmt.executeUpdate();
                    if (affectedRows == 0) {
                        throw new SQLException("Transfer failed, no account found.");
                    }
                }

                //Beneficiary Account
                try (PreparedStatement pstmt = conn.prepareStatement(insertTransactionSql)) {
                    pstmt.setString(1, beneficiaryAccountNumber);
                    pstmt.setString(2, Transaction.TransactionType.TRANSFER.name());
                    pstmt.setDouble(3, amount);
                    pstmt.setString(4, "From Acc:"+accountNumber+" Desc: "+description);
                    pstmt.setString(5, "SUCCESS");
                    pstmt.executeUpdate();
                }

                conn.commit();
                success = true;
                System.out.println("Transfer of $" + amount + " successful from account " + accountNumber+ "To Beneficiary Account "+ beneficiaryAccountNumber);
            } catch (SQLException e) {
                try {
                    conn.rollback();
                    // Insert failed transaction record
                    try (PreparedStatement pstmt = conn.prepareStatement(insertTransactionSql)) {
                        pstmt.setString(1, accountNumber);
                        pstmt.setString(2, Transaction.TransactionType.WITHDRAWAL.name());
                        pstmt.setDouble(3, amount);
                        pstmt.setString(4, description + " (Failed: " + e.getMessage() + ")");
                        pstmt.setString(5, "FAIL");
                        pstmt.executeUpdate();
                    }
                    conn.commit();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                e.printStackTrace();
                System.out.println("Withdrawal failed. Error: " + e.getMessage());
            } finally {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            return success;
        }));
    }
}