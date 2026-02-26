package com.example;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/hello")
public class HelloServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html><body>");
        out.println("<h1>Hello World from DevOps Pipeline!</h1>");
        out.println("<p>Deployed automatically by Jenkins</p>");
        out.println("<p>Code scanned by SonarQube</p>");
        out.println("<p>Artifact stored in Nexus</p>");
        out.println("<p>Deployed to Tomcat</p>");
        out.println("</body></html>");
    }
}
