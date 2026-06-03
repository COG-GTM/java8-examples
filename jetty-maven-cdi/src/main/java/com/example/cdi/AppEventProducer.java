package com.example.cdi;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;

/**
 * Fires {@link AppEvent}s through the CDI {@link Event} bus. Observers such as
 * {@link AppEventObserver} are notified synchronously when {@link #produce(String)}
 * is called.
 */
@ApplicationScoped
public class AppEventProducer {

    @Inject
    private Event<AppEvent> event;

    public void produce(String message) {
        event.fire(AppEvent.of(message));
    }
}
