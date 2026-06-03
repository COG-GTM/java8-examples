package eu.infomas.examples.cdi;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;

import javax.enterprise.event.Event;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;

public class MainServletTest {

    @SuppressWarnings("unchecked")
    private static void inject(MainServlet servlet, Greeting greeting, Event<String> event)
        throws Exception {
        Field greetingField = MainServlet.class.getDeclaredField("greeting");
        greetingField.setAccessible(true);
        greetingField.set(servlet, greeting);

        Field eventField = MainServlet.class.getDeclaredField("event");
        eventField.setAccessible(true);
        eventField.set(servlet, event);
    }

    @Test
    public void doGetWritesGreetingHtmlAndFiresEvent() throws Exception {
        MainServlet servlet = new MainServlet();
        Greeting greeting = mock(Greeting.class);
        when(greeting.getText()).thenReturn("Hello Test!");
        @SuppressWarnings("unchecked")
        Event<String> event = mock(Event.class);
        inject(servlet, greeting, event);

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        StringWriter buffer = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(buffer));

        servlet.doGet(request, response);

        String html = buffer.toString();
        verify(response).setContentType("text/html;charset=UTF-8");
        assertTrue("Expected greeting text in output but got: " + html,
            html.contains("<p>Hello Test!</p>"));
        assertTrue("Expected HTML document structure", html.contains("<title>Servlet MainServlet</title>"));
        verify(event).fire("Simple test event");
    }
}
