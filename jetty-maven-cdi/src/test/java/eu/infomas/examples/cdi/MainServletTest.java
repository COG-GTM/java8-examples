package eu.infomas.examples.cdi;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MainServletTest {

    @InjectMocks
    private MainServlet servlet;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Greeting greeting;

    @Mock
    private javax.enterprise.event.Event<String> event;

    private StringWriter stringWriter;

    @Before
    public void setUp() throws Exception {
        stringWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));
        when(greeting.getText()).thenReturn("Hello Test!");
    }

    @Test
    public void testDoGetSetsContentType() throws Exception {
        servlet.doGet(request, response);
        verify(response).setContentType("text/html;charset=UTF-8");
    }

    @Test
    public void testDoGetRendersHtml() throws Exception {
        servlet.doGet(request, response);
        String output = stringWriter.toString();
        assertTrue(output.contains("<!DOCTYPE html>"));
        assertTrue(output.contains("<html>"));
        assertTrue(output.contains("</html>"));
    }

    @Test
    public void testDoGetRendersGreeting() throws Exception {
        servlet.doGet(request, response);
        String output = stringWriter.toString();
        assertTrue(output.contains("Hello Test!"));
    }

    @Test
    public void testDoGetFiresEvent() throws Exception {
        servlet.doGet(request, response);
        verify(event).fire("Simple test event");
    }

    @Test
    public void testDoGetRendersTitle() throws Exception {
        servlet.doGet(request, response);
        String output = stringWriter.toString();
        assertTrue(output.contains("<title>Servlet MainServlet</title>"));
    }
}
