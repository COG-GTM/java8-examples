package com.example.cdi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.inject.Inject;
import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;

/**
 * Weld SE based unit tests for the CDI example beans. The container is bootstrapped
 * by the weld-junit5 extension; no servlet container is required.
 */
@EnableAutoWeld
@AddBeanClasses({GreetingService.class, AppEventProducer.class, AppEventObserver.class})
class CdiBeansTest {

    @Inject
    GreetingService greetingService;

    @Inject
    AppEventProducer eventProducer;

    @Inject
    AppEventObserver eventObserver;

    @Test
    void greetingServiceIsInjectedAndGreets() {
        assertNotNull(greetingService, "GreetingService should be injected by CDI");
        // No JNDI context in Weld SE, so the service falls back to the default name.
        assertEquals("Hello World!", greetingService.greet());
    }

    @Test
    void firedEventsAreObserved() {
        int before = eventObserver.getObservedCount();
        eventProducer.produce("first");
        eventProducer.produce("second");
        assertEquals(before + 2, eventObserver.getObservedCount(),
            "AppEventObserver should observe every fired AppEvent");
        assertTrue(eventObserver.getObservedCount() >= 2);
    }
}
