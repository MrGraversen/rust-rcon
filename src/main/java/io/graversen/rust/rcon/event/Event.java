package io.graversen.rust.rcon.event;

import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

@ToString
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Event {
    @Getter
    private final @NonNull LocalDateTime emittedAt = LocalDateTime.now();
    private final @NonNull Set<EventHandler> eventHandlers = new HashSet<>();

    public Set<EventHandler> getEventHandlers() {
        return Set.copyOf(eventHandlers);
    }

    protected Function<Object, String> eventHandlerNameMapper() {
        return object -> object.getClass().getSimpleName();
    }

    void registerEventHandler(@NonNull Object handler) {
        final var handlerName = eventHandlerNameMapper().apply(handler);
        eventHandlers.add(new EventHandler(handlerName));
    }
}
