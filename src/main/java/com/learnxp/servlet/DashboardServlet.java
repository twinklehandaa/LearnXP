package com.learnxp.servlet;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;
import com.learnxp.util.DBConnection;

public class DashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        req.getRequestDispatcher("navbar.html").include(req, resp);

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            resp.sendRedirect("login.html");
            return;
        }

        int userId = (Integer) session.getAttribute("userId");
        String username = (String) session.getAttribute("username");

        out.println("<!DOCTYPE html>");
        out.println("<html lang='en'>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<title>Dashboard | LearnXP</title>");
        out.println("<link rel='stylesheet' href='css/styles.css'>");

        out.println("<style>");
        out.println(".progress-bar-container { width: 100%; background: #f3f3f3; border-radius: 12px; overflow: hidden; height: 12px; margin-top: 8px; }");
        out.println(".progress-bar { height: 100%; background: linear-gradient(90deg, #007BFF, #00C6FF); border-radius: 12px; transition: width 0.4s ease; }");
        out.println(".xp-label { font-size: 0.9rem; color: #555; margin-top: 4px; }");
        out.println(".dashboard { display: flex; gap: 20px; flex-wrap: wrap; margin-top: 20px; }");
        out.println(".container { background-image: linear-gradient(to right, rgb(181, 202, 230, 0.8), rgba(209, 223, 235, 0.8)); }");
        out.println(".card { flex: 1 1 200px; background: #fff; padding: 20px; border-radius: 16px; box-shadow: 0 4px 10px rgba(0,0,0,0.1); }");
        out.println("</style>");

        out.println("</head>");
        out.println("<body>");
        out.println("<div class='container'>");
        out.println("<h1>Welcome, " + username + "!</h1>");
        out.println("<p>Ready to level up your learning today? 🎉</p>");
        out.println("</div>");

        out.println("<div class='dashboard'>");

        try (Connection con = DBConnection.getConnection()) {

            PreparedStatement ps = con.prepareStatement(
                "SELECT COUNT(*), NVL(AVG(SCORE_PERCENT), 0) FROM QUIZ_ATTEMPTS WHERE USERNAME = ?"
            );
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            int totalAttempts = 0;
            double avgScore = 0.0;
            if (rs.next()) {
                totalAttempts = rs.getInt(1);
                avgScore = rs.getDouble(2);
            }
            rs.close();
            ps.close();

            ps = con.prepareStatement("SELECT NVL(XP,0) FROM USERS WHERE ID = ?");
            ps.setInt(1, userId);
            rs = ps.executeQuery();
            int xp = 0;
            if (rs.next()) xp = rs.getInt(1);
            rs.close();
            ps.close();

            int level = (xp / 500) + 1;
            int xpForCurrentLevel = xp % 500;
            int progressPercent = (int)((xpForCurrentLevel / 500.0) * 100);

            out.println("<div class='card'>");
            out.println("<h3>Average Score</h3>");
            out.println("<p><strong>" + String.format("%.2f", avgScore) + "%</strong></p>");
            out.println("</div>");

            out.println("<div class='card'>");
            out.println("<h3>Level " + level + "</h3>");
            out.println("<div class='progress-bar-container'>");
            out.println("<div class='progress-bar' style='width:" + progressPercent + "%;'></div>");
            out.println("</div>");
            out.println("<div class='xp-label'>" + xpForCurrentLevel + " / 500 XP to next level</div>");
            out.println("</div>");

            out.println("<div class='card'>");
            out.println("<h3>Total Quiz Attempts</h3>");
            out.println("<p><strong>" + totalAttempts + "</strong></p>");
            out.println("</div>");  

        } catch (Exception e) {
            e.printStackTrace();
            out.println("<div class='card'><p style='color:red;'>Error loading dashboard: " + e.getMessage() + "</p></div>");
        }

        out.println("</div>"); 
        out.println("</body></html>");
    }
}
