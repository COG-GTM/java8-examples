package com.example.cdi;

import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * CDI-enabled servlet mapped to {@code /}. It demonstrates constructor-less field
 * injection of an {@link jakarta.enterprise.context.ApplicationScoped} bean and
 * firing a CDI event on each request.
 */
@WebServlet(name = "HelloServlet", urlPatterns = {"/"})
public class HelloServlet extends HttpServlet {

    @Inject
    private GreetingService greetingService;

    @Inject
    private AppEventProducer eventProducer;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head><title>jetty-maven-cdi</title></head>");
            out.println("<body>");
            out.println("<h1>" + greetingService.greet() + "</h1>");
            out.println("<p>Served by a CDI-injected servlet on Jetty 12 / Jakarta EE 10.</p>");
            out.println("</body>");
            out.println("</html>");
        }

        // Fire a CDI event; observed synchronously by AppEventObserver.
        eventProducer.produce("Request handled by HelloServlet");
    }
}
