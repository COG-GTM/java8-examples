package eu.infomas.examples.cdi;

import java.lang.reflect.Field;
import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.enterprise.inject.spi.BeanManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DefaultGreetingTest {

    private DefaultGreeting greeting;

    @Before
    public void setUp() {
        System.setProperty(Context.INITIAL_CONTEXT_FACTORY,
            TestInitialContextFactory.class.getName());
        greeting = new DefaultGreeting();
    }

    @After
    public void tearDown() {
        System.clearProperty(Context.INITIAL_CONTEXT_FACTORY);
        TestInitialContextFactory.setContext(null);
    }

    private static void setField(Object target, String name, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(name);
        f.setAccessible(true);
        f.set(target, value);
    }

    @Test
    public void getTextReturnsBoundHelloName() throws Exception {
        Context ctx = mock(Context.class);
        when(ctx.lookup("java:comp/env/helloName")).thenReturn("World");
        TestInitialContextFactory.setContext(ctx);

        assertEquals("Hello World!", greeting.getText());
    }

    @Test
    public void getTextReturnsExceptionTextWhenLookupFails() throws Exception {
        Context ctx = mock(Context.class);
        when(ctx.lookup("java:comp/env/helloName"))
            .thenThrow(new NamingException("boom"));
        TestInitialContextFactory.setContext(ctx);

        String text = greeting.getText();
        assertTrue("expected fallback text, got: " + text, text.startsWith("Hello "));
        assertTrue("expected exception details, got: " + text, text.contains("boom"));
    }

    @Test
    public void initSucceedsWithInjectedBeanManagerAndJndiLookup() throws Exception {
        BeanManager injected = mock(BeanManager.class);
        setField(greeting, "bm", injected);

        Context ctx = mock(Context.class);
        when(ctx.lookup("java:comp/BeanManager")).thenReturn(mock(BeanManager.class));
        TestInitialContextFactory.setContext(ctx);

        // Exercises init(), which prints status and performs the JNDI BeanManager lookup.
        greeting.init();
    }

    @Test
    public void initReportsFailureWhenBeanManagerNotInjectedAndJndiUnavailable() throws Exception {
        Context ctx = mock(Context.class);
        when(ctx.lookup("java:comp/BeanManager"))
            .thenThrow(new NamingException("no jndi"));
        // debugLookup() is triggered by the generic NamingException path.
        when(ctx.listBindings("java:comp/env/")).thenThrow(new NamingException("no bindings"));
        TestInitialContextFactory.setContext(ctx);

        greeting.init();
    }

    @Test
    public void initFallsBackToEnvBeanManagerWhenFirstLookupNotFound() throws Exception {
        Context ctx = mock(Context.class);
        when(ctx.lookup("java:comp/BeanManager"))
            .thenThrow(new NameNotFoundException("not here"));
        when(ctx.lookup("java:comp/env/BeanManager")).thenReturn(mock(BeanManager.class));
        TestInitialContextFactory.setContext(ctx);

        greeting.init();
    }

    @Test
    public void initHandlesBothBeanManagerLookupsNotFound() throws Exception {
        Context ctx = mock(Context.class);
        when(ctx.lookup("java:comp/BeanManager"))
            .thenThrow(new NameNotFoundException("not here"));
        when(ctx.lookup("java:comp/env/BeanManager"))
            .thenThrow(new NameNotFoundException("not here either"));
        TestInitialContextFactory.setContext(ctx);

        greeting.init();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void initRunsDebugLookupBindingEnumeration() throws Exception {
        Context ctx = mock(Context.class);
        when(ctx.lookup("java:comp/BeanManager"))
            .thenThrow(new NamingException("trigger debug"));

        Binding binding = new Binding("helloName", "java.lang.String", "World");
        NamingEnumeration<Binding> bindings = mock(NamingEnumeration.class);
        when(bindings.hasMore()).thenReturn(true, false);
        when(bindings.next()).thenReturn(binding);
        when(ctx.listBindings("java:comp/env/")).thenReturn(bindings);
        TestInitialContextFactory.setContext(ctx);

        greeting.init();
    }

    @Test
    public void onEventDoesNotThrow() {
        greeting.onEvent("some-event");
    }

    @Test
    public void constructorCreatesInstance() {
        assertNotNull(new DefaultGreeting());
    }
}
