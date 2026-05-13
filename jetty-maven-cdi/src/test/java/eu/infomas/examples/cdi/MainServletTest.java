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

    @Test
    public void testDoGet() throws Exception {
        MainServlet servlet = new MainServlet();

        Greeting greeting = mock(Greeting.class);
        when(greeting.getText()).thenReturn("Hello Test!");

        @SuppressWarnings("unchecked")
        Event<String> event = mock(Event.class);

        setField(servlet, "greeting", greeting);
        setField(servlet, "event", event);

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        servlet.doGet(request, response);

        verify(response).setContentType("text/html;charset=UTF-8");
        verify(event).fire("Simple test event");

        writer.flush();
        String html = stringWriter.toString();
        assertTrue(html.contains("<!DOCTYPE html>"));
        assertTrue(html.contains("<title>Servlet MainServlet</title>"));
        assertTrue(html.contains("Hello Test!"));
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
