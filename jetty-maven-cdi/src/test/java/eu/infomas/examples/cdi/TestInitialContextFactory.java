package eu.infomas.examples.cdi;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;

/**
 * A pluggable {@link InitialContextFactory} used only in tests so the JNDI lookups performed
 * by {@link DefaultGreeting} can be driven by a {@link Context} supplied by each test case.
 *
 * <p>Register it by setting the system property
 * {@code Context.INITIAL_CONTEXT_FACTORY} to this class' fully qualified name, then assign
 * the {@link Context} (typically a Mockito mock) to {@link #context}.
 */
public final class TestInitialContextFactory implements InitialContextFactory {

    /** The context returned to every {@code new InitialContext()} call. */
    public static volatile Context context;

    @Override
    public Context getInitialContext(Hashtable<?, ?> environment) throws NamingException {
        if (context == null) {
            throw new NamingException("No test Context configured");
        }
        return context;
    }
}
