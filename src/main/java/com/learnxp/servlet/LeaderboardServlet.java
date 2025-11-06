package com.learnxp.servlet;

import javax.servlet.*;
import javax.servlet.http.*;
import com.learnxp.util.DBConnection;
import java.io.*;
import java.sql.*;

public class LeaderboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();

        out.println("<html><head><title> Leaderboard | LearnXP</title><link rel='stylesheet' href='css/styles.css'></head><body>");
        req.getRequestDispatcher("navbar.html").include(req, resp);
        out.println("<div class='container'><h1>Leaderboard</h1>");

        try {
            Connection con = DBConnection.getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT NAME, XP FROM USERS ORDER BY XP DESC");

            int rank = 1;
            while (rs.next()) {
                out.println("<div class='card'><b>#" + rank + "</b> " + rs.getString("NAME") + " - " + rs.getInt("XP") + " XP</div>");
                rank++;
            }

            con.close();
        } catch (Exception e) {
            out.println("<p class = 'error'>Error loading leaderboard: " + e.getMessage() + "</p>");
        }

        out.println("</div></body></html>");
    }
}