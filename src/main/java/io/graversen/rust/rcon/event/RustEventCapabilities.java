package io.graversen.rust.rcon.event;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import java.util.Set;

@Getter
@ToString
public class RustEventCapabilities {
    private final @NonNull RustEventSourceStrategy strategy;
    private final @NonNull Set<Class<? extends RustEvent>> supportedEvents;

    public RustEventCapabilities(
            @NonNull RustEventSourceStrategy strategy,
            @NonNull Set<Class<? extends RustEvent>> supportedEvents
    ) {
        this.strategy = strategy;
        this.supportedEvents = Set.copyOf(supportedEvents);
    }

    public boolean supports(@NonNull Class<? extends RustEvent> eventClass) {
        return supportedEvents.contains(eventClass);
    }
}
