package com.oracle.repository.impl;

import com.oracle.Entity.Account;
import com.oracle.repository.AccountRepository;
import com.oracle.util.DatabaseConnection;

import java.sql.*;

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
}