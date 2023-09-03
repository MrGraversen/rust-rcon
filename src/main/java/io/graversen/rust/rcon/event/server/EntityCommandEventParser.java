package io.graversen.rust.rcon.event.server;

import io.graversen.rust.rcon.RustRconResponse;
import io.graversen.rust.rcon.event.BaseRustEventParser;
import io.graversen.rust.rcon.event.oxide.OxidePluginEvent;
import lombok.NonNull;

import java.util.Optional;
import java.util.function.Function;

public class EntityCommandEventParser extends BaseRustEventParser<EntityCommandEvent> {
    private static final String ENTITY_COMMAND_EVENT_PREFIX = "[ENTCMD]";

    @Override
    public boolean supports(@NonNull RustRconResponse payload) {
        final var message = payload.getMessage();
        return message.startsWith(ENTITY_COMMAND_EVENT_PREFIX);
    }

    @Override
    public Class<EntityCommandEvent> eventClass() {
        return EntityCommandEvent.class;
    }

    @Override
    protected Function<RustRconResponse, Optional<EntityCommandEvent>> eventParser() {
        return payload -> {
            final var message = payload.getMessage();
            final var prefix = message.substring(message.indexOf('[') + 1, message.indexOf(']')).trim();
            final var command = message.substring(prefix.length() + 2).trim();
            final var oxidePluginEvent = new EntityCommandEvent(payload.getServer(), command);
            return Optional.of(oxidePluginEvent);
        };
    }
}
