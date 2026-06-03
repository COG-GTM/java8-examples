package com.example.cdi;

import jakarta.inject.Inject;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;

/**
 * Servlet filter mapped to {@code /*} demonstrating CDI injection into a filter.
 * It logs each incoming request together with the greeting resolved by the
 * injected {@link GreetingService}.
 */
@WebFilter(filterName = "LoggingFilter", urlPatterns = {"/*"})
public class LoggingFilter implements Filter {

    private static final Logger LOG = System.getLogger(LoggingFilter.class.getName());

    @Inject
    private GreetingService greetingService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {

        if (request instanceof HttpServletRequest httpRequest) {
            LOG.log(Level.INFO, "Incoming request: {0} (greeting service says: {1})",
                httpRequest.getRequestURI(), greetingService.greet());
        }
        chain.doFilter(request, response);
    }
}
