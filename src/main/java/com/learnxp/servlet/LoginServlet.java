package com.learnxp.servlet;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;
import com.learnxp.util.DBConnection;

public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        req.setCharacterEncoding("UTF-8");
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        resp.setContentType("text/html;charset=UTF-8");

        try (PrintWriter out = resp.getWriter()) {

            if (email == null || password == null || email.trim().isEmpty() || password.isEmpty()) {
                resp.sendRedirect("login.html?error=missing");
                return;
            }

            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                         "SELECT ID, NAME, XP, LEVELNO FROM USERS WHERE EMAIL = ? AND PASSWORD = ?")) {

                stmt.setString(1, email.trim());
                stmt.setString(2, password); 
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String name = rs.getString("NAME");
                        int xp = rs.getInt("XP");
                        int level = rs.getInt("LEVELNO");
                        int userId = rs.getInt("ID");

                        HttpSession session = req.getSession(true);
                        session.setAttribute("userId", rs.getInt("ID"));
                        session.setAttribute("username", name);
                        session.setAttribute("email", email.trim());
                        session.setAttribute("xp", xp);
                        session.setAttribute("level", level);
                        session.setAttribute("userId", userId);

                        resp.sendRedirect("dashboard");
                        return;
                    } else {
                        resp.sendRedirect("login.html?error=invalid");
                        return;
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace(out);
                resp.sendRedirect("login.html?error=db");
            }
        }
    }
}