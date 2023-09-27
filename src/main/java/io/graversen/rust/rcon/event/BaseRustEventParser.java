package io.graversen.rust.rcon.event;

import io.graversen.rust.rcon.RustRconResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.function.Function;

@Slf4j
public abstract class BaseRustEventParser<T extends RustEvent> {
    public Optional<T> parseEvent(@NonNull RustRconResponse payload) {
        try {
            final var eventParser = eventParser();
            if (eventParser != null) {
                final var event = eventParser.apply(payload);
                if (event.isEmpty()) {
                    log.error(
                            "{} could not parse event even though it indicated to support payload: {}",
                            eventParser.getClass().getSimpleName(),
                            payload
                    );
                }
                return event;
            } else {
                log.error("Got null 'eventParser' instance!");
                return Optional.empty();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Optional.empty();
        }
    }

    public abstract boolean supports(@NonNull RustRconResponse payload);

    public abstract Class<T> eventClass();

    protected int order() {
        return Integer.MAX_VALUE;
    }

    protected abstract Function<RustRconResponse, Optional<T>> eventParser();
}
