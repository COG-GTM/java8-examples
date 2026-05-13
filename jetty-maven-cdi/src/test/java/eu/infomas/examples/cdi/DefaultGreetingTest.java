package eu.infomas.examples.cdi;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import javax.enterprise.inject.spi.BeanManager;
import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DefaultGreetingTest {

    private Context mockContext;
    private DefaultGreeting greeting;

    @Before
    public void setUp() {
        mockContext = mock(Context.class);
        TestInitialContextFactory.setMockContext(mockContext);
        System.setProperty(javax.naming.Context.INITIAL_CONTEXT_FACTORY,
            TestInitialContextFactory.class.getName());
        greeting = new DefaultGreeting();
    }

    @After
    public void tearDown() {
        System.clearProperty(javax.naming.Context.INITIAL_CONTEXT_FACTORY);
        TestInitialContextFactory.setMockContext(null);
    }

    @Test
    public void testGetTextWithJndiSuccess() throws Exception {
        when(mockContext.lookup("java:comp/env/helloName")).thenReturn("World");

        String result = greeting.getText();

        assertEquals("Hello World!", result);
    }

    @Test
    public void testGetTextWithJndiFailure() throws Exception {
        NamingException ex = new NamingException("test failure");
        when(mockContext.lookup("java:comp/env/helloName")).thenThrow(ex);

        String result = greeting.getText();

        assertTrue(result.startsWith("Hello "));
        assertTrue(result.contains("NamingException"));
    }

    @Test
    public void testOnEvent() {
        greeting.onEvent("test event");
    }

    @Test
    public void testInitWithBeanManagerInjected() throws Exception {
        BeanManager bm = createBeanManagerProxy();
        setBeanManagerField(greeting, bm);

        when(mockContext.lookup("java:comp/BeanManager")).thenReturn(bm);

        greeting.init();
    }

    @Test
    public void testInitWithNullBeanManager() throws Exception {
        BeanManager bm = createBeanManagerProxy();
        when(mockContext.lookup("java:comp/BeanManager"))
            .thenThrow(new NameNotFoundException("not found"));
        when(mockContext.lookup("java:comp/env/BeanManager"))
            .thenReturn(bm);

        greeting.init();
    }

    @Test
    public void testInitBeanManagerBothLookupsFail() throws Exception {
        when(mockContext.lookup("java:comp/BeanManager"))
            .thenThrow(new NameNotFoundException("not found"));
        when(mockContext.lookup("java:comp/env/BeanManager"))
            .thenThrow(new NameNotFoundException("not found either"));

        greeting.init();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testInitBeanManagerNamingExceptionTriggersDebug() throws Exception {
        when(mockContext.lookup("java:comp/BeanManager"))
            .thenThrow(new NamingException("general naming error"));

        NamingEnumeration<Binding> mockEnum = mock(NamingEnumeration.class);
        when(mockContext.listBindings("java:comp/env/")).thenReturn(mockEnum);
        when(mockEnum.hasMore()).thenReturn(true, false);
        Binding binding = new Binding("testName", "java.lang.String", "testValue");
        when(mockEnum.next()).thenReturn(binding);

        greeting.init();
    }

    @Test
    public void testInitBeanManagerDebugLookupFails() throws Exception {
        when(mockContext.lookup("java:comp/BeanManager"))
            .thenThrow(new NamingException("general naming error"));
        when(mockContext.listBindings("java:comp/env/"))
            .thenThrow(new NamingException("debug lookup failed"));

        greeting.init();
    }

    private BeanManager createBeanManagerProxy() {
        InvocationHandler handler = (proxy, method, args) -> null;
        return (BeanManager) Proxy.newProxyInstance(
            BeanManager.class.getClassLoader(),
            new Class[]{BeanManager.class},
            handler
        );
    }

    private void setBeanManagerField(DefaultGreeting target, BeanManager bm) throws Exception {
        Field field = DefaultGreeting.class.getDeclaredField("bm");
        field.setAccessible(true);
        field.set(target, bm);
    }
}
