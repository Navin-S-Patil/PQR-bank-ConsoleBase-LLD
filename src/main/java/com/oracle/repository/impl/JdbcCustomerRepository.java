package com.oracle.repository.impl;

import com.oracle.Entity.Account;
import com.oracle.Entity.Customer;
import com.oracle.repository.CustomerRepository;
import com.oracle.util.DatabaseConnection;

import java.sql.*;
import java.util.Optional;

public class JdbcCustomerRepository implements CustomerRepository {

    @Override
    public Customer save(Customer customer) {
        String sql = "INSERT INTO customers (first_name, last_name, email, phone,address, dob, password) VALUES (?, ?, ?, ?,?, ?, ?)";

        return DatabaseConnection.executeWithConnection(conn -> {
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, customer.getFirstName());
                pstmt.setString(2, customer.getLastName());
                pstmt.setString(3, customer.getEmail());
                pstmt.setString(4, customer.getPhone());
                pstmt.setString(5, customer.getAddress());
                pstmt.setDate(6, new java.sql.Date(customer.getDob().getTime()));
                pstmt.setString(7, customer.getPassword());

                int affectedRows = pstmt.executeUpdate();

                if (affectedRows == 0) {
                    throw new SQLException("Creating customer failed, no rows affected.");
                }

                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        customer.setId(generatedKeys.getLong(1));
                    } else {
                        throw new SQLException("Creating customer failed, no ID obtained.");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
            return customer;
        });
    }

    @Override
    public boolean existsByEmailOrPhone(String email, String phone) {
        String sql = "SELECT COUNT(*) FROM customers WHERE email = ? OR phone = ?";

        return Boolean.TRUE.equals(DatabaseConnection.executeWithConnection(conn -> {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, email);
                pstmt.setString(2, phone);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1) > 0;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        }));
    }

    @Override
    public Customer getCustomerByCustomerId(Long id) {
        String sql = "SELECT * FROM customers WHERE id = ?";
        return DatabaseConnection.executeWithConnection(conn ->{
            Customer customer = null;
            try(PreparedStatement pstmt = conn.prepareStatement(sql)){
                pstmt.setLong(1,id);

                try(ResultSet rs = pstmt.executeQuery()){
                    if (rs.next()) {
                        customer = new Customer();
                        customer.setId(rs.getLong("id"));
                        customer.setFirstName(rs.getString("first_name"));
                        customer.setLastName(rs.getString("last_name"));
                        customer.setEmail(rs.getString("email"));
                        customer.setPhone(rs.getString("phone"));
                        customer.setAddress(rs.getString("address"));
                        customer.setDob(rs.getDate("dob"));
                        customer.setPassword(rs.getString("password"));
                    }
                }

            }catch (SQLException e){
                e.printStackTrace();
            }
            return customer;
        });
    }

    @Override
    public boolean verifyCustomerPassword(Long customerId, String password) {
        Customer customer = getCustomerByCustomerId(customerId);
        if (customer != null) {
            return customer.getPassword().equals(password);
        }
        return false;
    }

    @Override
    public Optional<Customer> customerLogin(String email, String password) {
        String sql = "SELECT * FROM customers WHERE email=? AND password=?";
        return DatabaseConnection.executeWithConnection(connection -> {
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, email);
                pstmt.setString(2, password);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        Customer customer = new Customer();
                        customer.setId(rs.getLong("id"));
                        customer.setFirstName(rs.getString("first_name"));
                        customer.setLastName(rs.getString("last_name"));
                        customer.setAddress(rs.getString("address"));
                        customer.setEmail(rs.getString("email"));
                        customer.setPhone(rs.getString("phone"));
                        customer.setDob(rs.getDate("dob"));
                        return Optional.of(customer);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return Optional.empty();
        });
    }



}
