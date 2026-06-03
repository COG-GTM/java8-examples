package com.example.cdi;

import java.time.Instant;

/**
 * Immutable payload fired through the CDI event bus.
 */
public record AppEvent(String message, Instant firedAt) {

    public static AppEvent of(String message) {
        return new AppEvent(message, Instant.now());
    }
}
