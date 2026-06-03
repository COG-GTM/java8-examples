package com.example.cdi;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Observes {@link AppEvent}s fired by {@link AppEventProducer} via {@code @Observes}
 * and keeps a count of how many events have been seen.
 */
@ApplicationScoped
public class AppEventObserver {

    private static final Logger LOG = System.getLogger(AppEventObserver.class.getName());

    private final AtomicInteger observed = new AtomicInteger();

    public void onAppEvent(@Observes AppEvent event) {
        int count = observed.incrementAndGet();
        LOG.log(Level.INFO, "Observed AppEvent #{0}: {1} (firedAt={2})",
            count, event.message(), event.firedAt());
    }

    public int getObservedCount() {
        return observed.get();
    }
}
