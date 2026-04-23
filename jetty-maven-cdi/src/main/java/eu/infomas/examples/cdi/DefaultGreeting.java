package eu.infomas.examples.cdi;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.inject.Inject;
import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

@ApplicationScoped
public class DefaultGreeting implements Greeting {

    @Inject
    private BeanManager bm;

    // In a CDI environment, keep your constructors very cheap to create.
    // The constructor if an ApplicationScoped bean is NOT guaranteed to be called exactly
    // one time. Weld calls the constructor twice (for the actual instance and the proxy)
    public DefaultGreeting() {
        System.out.println("new DefaultGreeting()");
    }

    // Guaranteed to be called exactly one time and after all dependencies resolved
    // http://stackoverflow.com/questions/3406555/why-use-postconstruct
    @PostConstruct
    public void init() {
        System.out.println("DefaultGreeting#init()");
        System.out.printf("BeanManager injection %s%n", bm == null ? "failed" : "succeeded");
        // try to lookup by JNDI
        System.out.printf("BeanManager JNDI lookup %s%n", lookupBeanManager() == null ?
            "failed" : "succeeded");
    }

    @Override
    public String getText() {
        String helloName;
        try {
            Context ctx = new InitialContext();
            helloName = (String)ctx.lookup("java:comp/env/helloName");
        } catch (NamingException ex) {
            helloName = ex.toString();
        }
        return String.format("Hello %s!", helloName);
    }

    public void onEvent(@Observes String event) {
        System.out.println("Observed event: " + event);
    }

    /**
     * Any bean or other Jakarta EE component which supports injection can obtain an instance of
     * `BeanManager` via injection.
     * Jakarta EE components may obtain an instance of `BeanManager` from JNDI by looking up the
     * name `java:comp/BeanManager`, or via the CDI API using `CDI.current().getBeanManager()`.
     * Any operation of `BeanManager` may be called at any time during the execution of the
     * application.
     */
    private BeanManager lookupBeanManager() {
        // Preferred approach: use CDI API directly
        try {
            return CDI.current().getBeanManager();
        } catch (IllegalStateException ex) {
            System.err.println("CDI.current() not available: " + ex);
        }
        // Fallback: try JNDI lookup
        try {
            final Context ctx = new InitialContext();
            try {
                return (BeanManager) ctx.lookup("java:comp/BeanManager");
            } catch (NameNotFoundException nf1) {
                System.err.println("Lookup java:comp/BeanManager failed: " + nf1);
                try {
                    return (BeanManager) ctx.lookup("java:comp/env/BeanManager");
                } catch (NameNotFoundException nf2) {
                    System.err.println("Lookup java:comp/env/BeanManager failed: " + nf2);
                }
            }
        } catch (NamingException ex) {
            System.err.println(ex);
            debugLookup();
        }
        return null;
    }

    private void debugLookup() {
        try {
            Context ctx = new InitialContext();
            //System.out.println("Context Environment: " + ctx.getEnvironment());
            NamingEnumeration e = ctx.listBindings("java:comp/env/");
            while (e.hasMore()) {
                Binding binding = (Binding)e.next();
                System.out.println("---");
                System.out.println("Name: " + binding.getName());
                System.out.println("Type: " + binding.getClassName());
                System.out.println("Value: " + binding.getObject());
            }
            e.close();
        } catch (NamingException ex) {
            ex.printStackTrace();
        }
    }

}
