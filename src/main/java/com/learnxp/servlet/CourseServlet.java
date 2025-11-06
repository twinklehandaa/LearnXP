package com.learnxp.servlet;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;
import com.learnxp.util.DBConnection;

public class CourseServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        out.println("<html><head>");
        out.println("<link rel='stylesheet' href='css/styles.css'>");
        out.println("<title>Courses | LearnXP</title>");
        out.println("</head><body>");

        req.getRequestDispatcher("navbar.html").include(req, resp);

        out.println("<div class='main-content'>");
        out.println("<h1>📚 Available Courses</h1>");
        out.println("<p style='color:#4b5563;margin-bottom:2rem;'>Grow your skills, one level at a time!</p>");
        out.println("<div class='course-grid'>");

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM COURSES ORDER BY LEVELNO")) {

            boolean found = false;

            while (rs.next()) {
                found = true;
                out.println("<div class='course-card'>");
                out.println("<h3>" + rs.getString("TITLE") + "</h3>");
                out.println("<p class='course-desc'>" + rs.getString("DESCRIPTION") + "</p>");
                out.println("<p class='course-meta'><b>Level:</b> " + rs.getString("LEVELNO") +
                            " &nbsp; | &nbsp; <b>Duration:</b> " + rs.getString("DURATION") + "</p>");
                out.println("<a class='btn' href='" + rs.getString("LINK") + "' target='_blank'>🎥 Watch Course</a>");
                out.println("</div>");
            }

            if (!found) {
                out.println("<p>No courses available right now. Check back soon!</p>");
            }

        } catch (SQLException e) {
            out.println("<p class='error'>Error loading courses: " + e.getMessage() + "</p>");
            e.printStackTrace(out);
        }

        out.println("</div>"); 
        out.println("</div>"); 
        out.println("</body></html>");
    }
}
