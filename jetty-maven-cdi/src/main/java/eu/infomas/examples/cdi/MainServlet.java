package eu.infomas.examples.cdi;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// Using Servlet 3.0+
@WebServlet(name = "MainServlet", urlPatterns = {"/"})
public class MainServlet extends HttpServlet {

    @Inject
    private Greeting greeting;
    @Inject
    private jakarta.enterprise.event.Event<String> event;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet MainServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<p>" + greeting.getText() + "</p>");
            out.println("</body>");
            out.println("</html>");
        }

        // Test CDI event support, this event is observed by the DefaultGreeting class
        event.fire("Simple test event");
    }

}
