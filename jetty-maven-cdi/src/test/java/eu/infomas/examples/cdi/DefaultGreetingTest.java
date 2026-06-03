package eu.infomas.examples.cdi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;

import javax.enterprise.inject.spi.BeanManager;
import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DefaultGreetingTest {

    private Context ctx;

    @Before
    public void setUp() {
        System.setProperty(Context.INITIAL_CONTEXT_FACTORY,
            TestInitialContextFactory.class.getName());
        ctx = mock(Context.class);
        TestInitialContextFactory.context = ctx;
    }

    @After
    public void tearDown() {
        System.clearProperty(Context.INITIAL_CONTEXT_FACTORY);
        TestInitialContextFactory.context = null;
    }

    private static void setBeanManager(DefaultGreeting greeting, BeanManager bm) throws Exception {
        Field field = DefaultGreeting.class.getDeclaredField("bm");
        field.setAccessible(true);
        field.set(greeting, bm);
    }

    @Test
    public void getTextReturnsBoundNameWhenLookupSucceeds() throws Exception {
        when(ctx.lookup("java:comp/env/helloName")).thenReturn("World");

        assertEquals("Hello World!", new DefaultGreeting().getText());
    }

    @Test
    public void getTextFallsBackToExceptionMessageWhenLookupFails() throws Exception {
        when(ctx.lookup("java:comp/env/helloName"))
            .thenThrow(new NamingException("lookup failed"));

        String text = new DefaultGreeting().getText();

        assertTrue("Expected exception message in greeting but got: " + text,
            text.startsWith("Hello javax.naming.NamingException"));
    }

    @Test
    public void initSucceedsWhenBeanManagerInjectedAndJndiLookupSucceeds() throws Exception {
        DefaultGreeting greeting = new DefaultGreeting();
        setBeanManager(greeting, mock(BeanManager.class));
        when(ctx.lookup("java:comp/BeanManager")).thenReturn(mock(BeanManager.class));

        greeting.init();

        verify(ctx).lookup("java:comp/BeanManager");
    }

    @Test
    public void initFallsBackToContainerSpecificBeanManagerName() throws Exception {
        when(ctx.lookup("java:comp/BeanManager")).thenThrow(new NameNotFoundException());
        when(ctx.lookup("java:comp/env/BeanManager")).thenReturn(mock(BeanManager.class));

        new DefaultGreeting().init();

        verify(ctx).lookup("java:comp/env/BeanManager");
    }

    @Test
    public void initHandlesBeanManagerNotFoundUnderEitherName() throws Exception {
        when(ctx.lookup("java:comp/BeanManager")).thenThrow(new NameNotFoundException());
        when(ctx.lookup("java:comp/env/BeanManager")).thenThrow(new NameNotFoundException());

        new DefaultGreeting().init();

        verify(ctx).lookup("java:comp/env/BeanManager");
    }

    @Test
    public void initTriggersDebugLookupOnGeneralNamingException() throws Exception {
        when(ctx.lookup("java:comp/BeanManager"))
            .thenThrow(new NamingException("context unavailable"));

        @SuppressWarnings("unchecked")
        NamingEnumeration<Binding> bindings = mock(NamingEnumeration.class);
        when(bindings.hasMore()).thenReturn(true, false);
        when(bindings.next()).thenReturn(new Binding("helloName", "World"));
        when(ctx.listBindings("java:comp/env/")).thenReturn(bindings);

        new DefaultGreeting().init();

        verify(ctx).listBindings("java:comp/env/");
        verify(bindings).close();
    }

    @Test
    public void onEventDoesNotThrow() {
        new DefaultGreeting().onEvent("Simple test event");
    }
}
