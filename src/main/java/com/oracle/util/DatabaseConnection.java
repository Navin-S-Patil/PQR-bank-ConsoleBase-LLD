package com.oracle.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.function.Function;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/testdb";
    private static final String USER = "root";
    private static final String PASSWORD = "Mumbai@123";

    public static Connection getConnection() {

        Connection connection = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Driver Class is Loaded");

            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.err.println("Connection failed: " + e.getMessage());
        }catch (ClassNotFoundException e){
            System.err.println("Connection failed: Class Not Found" + e.getMessage());
        }
        return connection;
    }


    public static <R> R executeWithConnection(Function<Connection, R> action) {
        try (Connection conn = getConnection()) {
            if (conn != null) {
                return action.apply(conn);
            } else {
                throw new SQLException("Failed to establish connection.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
