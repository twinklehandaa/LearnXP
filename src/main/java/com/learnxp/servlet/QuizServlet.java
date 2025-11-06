package com.learnxp.servlet;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;
import com.learnxp.util.DBConnection;

public class QuizServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            resp.sendRedirect("login.html");
            return;
        }

        String username = (String) session.getAttribute("username");
        String quizName = req.getParameter("name");

        req.getRequestDispatcher("navbar.html").include(req, resp);

        if (quizName != null) {
            String filePath = getServletContext().getRealPath("/quizzes/" + quizName);
            File file = new File(filePath);
            if (!file.exists()) {
                out.println("<h3 style='color:red;'>Quiz file not found: " + quizName + "</h3>");
                return;
            }

            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.contains("<head>")) {
                        out.println(line);
                        out.println("<base href='" + req.getContextPath() + "/'>");
                    } else {
                        out.println(line);
                    }
                }
            } catch (IOException e) {
                out.println("<p style='color:red;'>Error reading quiz file: " + e.getMessage() + "</p>");
                e.printStackTrace();
            }
            return;
        }

        out.println("<html><head><title>Quizzes | LearnXP</title>");
        out.println("<link rel='stylesheet' href='css/styles.css'>");
        out.println("</head><body>");
        out.println("<div class='container'>");
        out.println("<h2>Available Quizzes</h2>");

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT Q.ID, Q.TITLE, Q.HTML_FILE, " +
                     "(SELECT MAX(SCORE_PERCENT) FROM QUIZ_ATTEMPTS QA WHERE QA.QUIZ_ID = Q.ID AND QA.USERNAME = ?) AS SCORE " +
                     "FROM QUIZZES Q ORDER BY Q.ID"
             )) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String title = rs.getString("TITLE");
                String htmlFile = rs.getString("HTML_FILE");
                double score = rs.getDouble("SCORE"); 

                out.println("<div class='quiz-card'>");
                out.println("<a href='quiz?name=" + htmlFile + "'>" + title + "</a>");

                if (score != 0) {
                    if (score >= 70.0) {
                        out.println(" <span class='badge completed'>✅ Completed</span>");
                    } else {
                        out.println(" <span class='badge attempted'>⚠️ Attempted</span>");
                    }
                }

                out.println("</div>");
            }

        } catch (Exception e) {
            out.println("<p class='error'>Error loading quizzes: " + e.getMessage() + "</p>");
            e.printStackTrace();
        }

        out.println("<style>");
        out.println(".badge.completed { color: white; background: #10b981; padding: 2px 6px; border-radius: 6px; margin-left: 10px; }");
        out.println(".badge.attempted { color: white; background: #f59e0b; padding: 2px 6px; border-radius: 6px; margin-left: 10px; }");
        out.println("</style>");

        out.println("</div></body></html>");
    }
}