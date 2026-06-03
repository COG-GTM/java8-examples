package eu.infomas.examples.cdi;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;

import javax.enterprise.event.Event;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MainServletTest {

    @SuppressWarnings("unchecked")
    private static void setField(Object target, String name, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(name);
        f.setAccessible(true);
        f.set(target, value);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void doGetWritesGreetingAndFiresEvent() throws Exception {
        MainServlet servlet = new MainServlet();

        Greeting greeting = mock(Greeting.class);
        when(greeting.getText()).thenReturn("Hello Test!");
        Event<String> event = mock(Event.class);

        setField(servlet, "greeting", greeting);
        setField(servlet, "event", event);

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        StringWriter sw = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(sw));

        servlet.doGet(request, response);

        String html = sw.toString();
        verify(response).setContentType("text/html;charset=UTF-8");
        assertTrue("expected greeting text in output: " + html, html.contains("Hello Test!"));
        assertTrue("expected html document in output: " + html, html.contains("<!DOCTYPE html>"));
        assertTrue("expected title in output: " + html, html.contains("Servlet MainServlet"));
        verify(event).fire("Simple test event");
        verify(greeting).getText();
    }
}
