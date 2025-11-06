package com.learnxp.listener;

import javax.servlet.*;
import javax.servlet.annotation.WebListener;
import java.sql.*;
import java.util.Enumeration;

@WebListener
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // nothing required here for now
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // try to deregister JDBC drivers to avoid memory leaks on redeploy
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            try {
                DriverManager.deregisterDriver(driver);
                System.out.println("Deregistered JDBC driver: " + driver);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
