package com.learnxp.servlet;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;
import com.learnxp.util.DBConnection;

public class SignupServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        req.setCharacterEncoding("UTF-8");
        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        resp.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = resp.getWriter()) {

            if (name == null || email == null || password == null ||
                name.trim().isEmpty() || email.trim().isEmpty() || password.trim().isEmpty()) {
                resp.sendRedirect("signup.html?error=missing");
                return;
            }

            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement checkStmt = conn.prepareStatement(
                         "SELECT ID FROM USERS WHERE EMAIL = ?")) {

                checkStmt.setString(1, email.trim());
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        resp.sendRedirect("signup.html?error=exists");
                        return;
                    }
                }

                try (PreparedStatement insert = conn.prepareStatement(
                        "INSERT INTO USERS (ID, NAME, EMAIL, PASSWORD, XP, LEVELNO) " +
                                "VALUES (USERS_SEQ.NEXTVAL, ?, ?, ?, 0, 1)")) {
                    insert.setString(1, name.trim());
                    insert.setString(2, email.trim());
                    insert.setString(3, password); 
                    int rows = insert.executeUpdate();
                    if (rows > 0) {
                        resp.sendRedirect("login.html?signup=success");
                    } else {
                        resp.sendRedirect("signup.html?error=failed");
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace(out);
                resp.sendRedirect("signup.html?error=db");
            }
        }
    }
}