package eu.infomas.examples.cdi;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.spi.InitialContextFactory;

/**
 * Test-only JNDI {@link InitialContextFactory} that returns a {@link Context}
 * supplied by the test. This lets unit tests drive the JNDI lookups performed by
 * {@link DefaultGreeting} without booting a real CDI/Jetty container.
 */
public final class TestInitialContextFactory implements InitialContextFactory {

    private static Context context;

    public static void setContext(Context ctx) {
        context = ctx;
    }

    @Override
    public Context getInitialContext(Hashtable<?, ?> environment) {
        return context;
    }
}
