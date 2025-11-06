package com.learnxp.servlet;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;

public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        out.println("<!DOCTYPE html>");
        out.println("<html lang='en'>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<title>Logout | LearnXP</title>");
        out.println("<link rel='stylesheet' href='css/styles.css'>");
        out.println("</head>");
        out.println("<body class='logout'>");
        out.println("<div class='logout-message'>");
        out.println("<h2>You’ve been logged out successfully 👋</h2>");
        out.println("<p>Thanks for learning with <strong>LearnXP</strong>!</p>");
        out.println("<br>");
        out.println("<a href='login.html'><button>Login Again</button></a>");
        out.println("</div>");
        out.println("</body>");
        out.println("</html>");
    }
}