package com.learnxp.servlet;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;
import com.learnxp.util.DBConnection;
import java.util.*;

public class QuizAttemptServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            resp.sendRedirect("login.html");
            return;
        }

        int userId = (Integer) session.getAttribute("userId");
        String username = (String) session.getAttribute("username");
        int quizId = Integer.parseInt(req.getParameter("quiz_id"));

        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = DBConnection.getConnection();

            Map<Integer, String> correctAnswers = new HashMap<>();
            if (quizId == 1) { // SQL Fundamentals
                correctAnswers.put(1, "B");
                correctAnswers.put(2, "B");
                correctAnswers.put(3, "C");
            } else if (quizId == 2) { // Java Basics
                correctAnswers.put(1, "B");
                correctAnswers.put(2, "D");
                correctAnswers.put(3, "A");
            } else if (quizId == 3) { // Data Structures
                correctAnswers.put(1, "B");
                correctAnswers.put(2, "B");
                correctAnswers.put(3, "C");
            } else if (quizId == 4) { // Python Basics
                correctAnswers.put(1, "B");
                correctAnswers.put(2, "A");
                correctAnswers.put(3, "D");
            } else if (quizId == 5) { // HTML & CSS
                correctAnswers.put(1, "A");
                correctAnswers.put(2, "B");
                correctAnswers.put(3, "C");
            } else if (quizId == 6) { // JavaScript Fundamentals
                correctAnswers.put(1, "B");
                correctAnswers.put(2, "C");
                correctAnswers.put(3, "A");
            } else if (quizId == 7) { // Data Science Intro
                correctAnswers.put(1, "B");
                correctAnswers.put(2, "D");
                correctAnswers.put(3, "C");
            } else if (quizId == 8) { // Machine Learning Basics
                correctAnswers.put(1, "A");
                correctAnswers.put(2, "B");
                correctAnswers.put(3, "D");
            } else if (quizId == 9) { // Cybersecurity Essentials
                correctAnswers.put(1, "C");
                correctAnswers.put(2, "B");
                correctAnswers.put(3, "A");
            } else if (quizId == 10) { // Algorithms Basics
                correctAnswers.put(1, "A");
                correctAnswers.put(2, "B");
                correctAnswers.put(3, "C");
            }

            int total = correctAnswers.size();
            int correct = 0;

            for (Integer questionId : correctAnswers.keySet()) {
                String userAnswer = req.getParameter("q" + questionId);
                String correctAnswer = correctAnswers.get(questionId);
                if (userAnswer != null && userAnswer.equalsIgnoreCase(correctAnswer)) {
                    correct++;
                }
            }

            double percentage = (total > 0) ? ((double) correct / total) * 100 : 0.0;
            boolean passed = percentage >= 70.0;

            double previousMaxScore = 0;
            ps = con.prepareStatement(
                    "SELECT MAX(SCORE_PERCENT) AS MAX_SCORE FROM QUIZ_ATTEMPTS WHERE USERNAME = ? AND QUIZ_ID = ?");
            ps.setString(1, username);
            ps.setInt(2, quizId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                previousMaxScore = rs.getDouble("MAX_SCORE");
            }
            rs.close();
            ps.close();

            boolean firstPass = passed && previousMaxScore < 70.0;

            ps = con.prepareStatement(
                    "INSERT INTO QUIZ_ATTEMPTS (USERNAME, QUIZ_ID, SCORE_PERCENT, ATTEMPT_DATE) VALUES (?, ?, ?, SYSDATE)");
            ps.setString(1, username);
            ps.setInt(2, quizId);
            ps.setDouble(3, percentage);
            ps.executeUpdate();
            ps.close();

            if (firstPass) {
                ps = con.prepareStatement(
                        "UPDATE USERS SET XP = NVL(XP,0) + 50 WHERE ID = ?");
                ps.setInt(1, userId);
                ps.executeUpdate();
                ps.close();
            }

            out.println("<html><head><title>Quiz Result | LearnXP</title>");
            out.println("<link rel='stylesheet' href='css/styles.css'>");
            out.println("</head><body>");
            req.getRequestDispatcher("navbar.html").include(req, resp);
            out.println("<div class='container'>");
            out.println("<div class='card'>");
            out.println("<h1>Quiz Result</h1>");
            out.println("<div class='score'>You scored <b>" + correct + "</b> out of <b>" + total + "</b> ("
                    + String.format("%.1f", percentage) + "%)</div>");

            if (passed) {
                out.println("<div class='pass'>🎉 Congratulations! You passed!</div>");
                if (firstPass) {
                    out.println("<div class='xp'>+50 XP added to your profile!</div>");
                } else {
                    out.println("<div class='xp'>You already received XP for passing this quiz before!</div>");
                }
            } else {
                out.println("<div class='fail'>❌ Keep practicing! You need at least 70% to earn XP.</div>");
            }

            out.println("<br><a href='dashboard'>Back to Dashboard</a>");
            out.println("</div></div></body></html>");

        } catch (Exception e) {
            e.printStackTrace(out);
        } finally {
            try {
                if (ps != null)
                    ps.close();
            } catch (Exception ignored) {
            }
            try {
                if (con != null)
                    con.close();
            } catch (Exception ignored) {
            }
        }
    }
}