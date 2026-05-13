package eu.infomas.examples.cdi;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.spi.InitialContextFactory;

/**
 * A test-only JNDI InitialContextFactory that returns a configurable mock context.
 */
public class TestInitialContextFactory implements InitialContextFactory {

    private static Context mockContext;

    public static void setMockContext(Context context) {
        mockContext = context;
    }

    @Override
    public Context getInitialContext(Hashtable<?, ?> environment) {
        return mockContext;
    }
}
