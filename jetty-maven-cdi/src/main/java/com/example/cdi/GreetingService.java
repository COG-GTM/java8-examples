package com.example.cdi;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Application-scoped CDI bean that produces a greeting message.
 *
 * <p>The name used in the greeting is read from the {@code java:comp/env/helloName}
 * JNDI environment entry (defined in {@code web.xml} and overridden by
 * {@code web-overwrite.xml} when running through the Jetty Maven plugin). When no
 * JNDI context is available (for example in a Weld SE unit test) it falls back to
 * a default name.
 */
@ApplicationScoped
public class GreetingService {

    private static final Logger LOG = System.getLogger(GreetingService.class.getName());

    private static final String DEFAULT_NAME = "World";

    @PostConstruct
    void init() {
        LOG.log(Level.INFO, "GreetingService initialized");
    }

    public String greet() {
        return String.format("Hello %s!", resolveName());
    }

    private String resolveName() {
        try {
            Context ctx = new InitialContext();
            Object value = ctx.lookup("java:comp/env/helloName");
            if (value != null) {
                return value.toString();
            }
        } catch (NamingException ex) {
            LOG.log(Level.DEBUG, "helloName JNDI lookup unavailable, using default", ex);
        }
        return DEFAULT_NAME;
    }
}
