package com.learnxp.util;

import java.sql.*;

public class DBConnection {
    private static final String URL = "jdbc:oracle:thin:@localhost:1521:xe";
    private static final String USER = "system";
    private static final String PASSWORD = "25MCA0052";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            System.out.println("Oracle JDBC Driver Loaded Successfully");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Oracle JDBC Driver not found.", e);
        }
        Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
        System.out.println("✅ Connected to DB: " + conn);
        return conn;
    }
}
