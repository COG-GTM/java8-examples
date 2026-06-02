package eu.infomas.examples.cdi;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class DefaultGreetingTest {

    @Test
    public void getTextReturnsHelloGreeting() {
        Greeting greeting = new DefaultGreeting();
        String text = greeting.getText();
        assertTrue("Greeting should start with 'Hello '", text.startsWith("Hello "));
        assertTrue("Greeting should end with '!'", text.endsWith("!"));
    }

}
