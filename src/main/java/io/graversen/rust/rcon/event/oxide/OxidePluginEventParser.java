package io.graversen.rust.rcon.event.oxide;

import io.graversen.rust.rcon.RustRconResponse;
import io.graversen.rust.rcon.event.BaseRustEventParser;
import lombok.NonNull;

import java.util.Optional;
import java.util.function.Function;

public class OxidePluginEventParser extends BaseRustEventParser<OxidePluginEvent> {
    private static final String EVENT_PATTERN = "^\\[(.*?)\\].*";
    private static final String NATIVE_EVENT_PREFIX = "[event]";

    @Override
    protected Function<RustRconResponse, Optional<OxidePluginEvent>> eventParser() {
        return payload -> {
            final var message = payload.getMessage();
            final var pluginName = message.substring(message.indexOf('[') + 1, message.indexOf(']')).trim();
            final var pluginMessage = message.substring(pluginName.length() + 2).trim();
            final var oxidePluginEvent = new OxidePluginEvent(pluginName, pluginMessage);
            return Optional.of(oxidePluginEvent);
        };
    }

    @Override
    public boolean supports(@NonNull RustRconResponse payload) {
        final var message = payload.getMessage();
        if (message.matches(EVENT_PATTERN)) {
            final var prefix = message.substring(message.indexOf('['), message.indexOf(']') + 1);
            return !prefix.equals(NATIVE_EVENT_PREFIX);
        }

        return false;
    }

    @Override
    public Class<OxidePluginEvent> eventClass() {
        return OxidePluginEvent.class;
    }
}