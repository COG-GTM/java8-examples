package eu.infomas.examples.cdi;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class DefaultGreetingTest {

    private DefaultGreeting greeting;

    @Before
    public void setUp() {
        greeting = new DefaultGreeting();
    }

    @Test
    public void testImplementsGreetingInterface() {
        assertTrue(greeting instanceof Greeting);
    }

    @Test
    public void testGetTextReturnsNonNull() {
        String text = greeting.getText();
        assertNotNull(text);
    }

    @Test
    public void testGetTextFormat() {
        String text = greeting.getText();
        assertTrue("Expected greeting to start with 'Hello '", text.startsWith("Hello "));
        assertTrue("Expected greeting to end with '!'", text.endsWith("!"));
    }

    @Test
    public void testGetTextWithoutJndiReturnsErrorGreeting() {
        // Without a JNDI context, getText() catches NamingException and uses it as the name
        String text = greeting.getText();
        assertNotNull(text);
        assertTrue(text.startsWith("Hello "));
    }

    @Test
    public void testOnEventDoesNotThrow() {
        greeting.onEvent("test event");
    }

    @Test
    public void testConstructorDoesNotThrow() {
        DefaultGreeting g = new DefaultGreeting();
        assertNotNull(g);
    }
}
