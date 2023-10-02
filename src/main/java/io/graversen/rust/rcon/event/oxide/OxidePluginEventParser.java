package io.graversen.rust.rcon.event.oxide;

import io.graversen.rust.rcon.RustRconResponse;
import io.graversen.rust.rcon.event.BaseRustEventParser;
import lombok.NonNull;

import java.util.Optional;
import java.util.function.Function;

public class OxidePluginEventParser extends BaseRustEventParser<OxidePluginEvent> {
    private static final String EVENT_PATTERN = "^\\[(.*?)\\].*";
    private static final String NATIVE_EVENT_PREFIX = "[event]";
    private static final String CHAT_EVENT_PREFIX = "[CHAT]";
    private static final String TEAM_CHAT_EVENT_PREFIX = "[TEAM CHAT]";
    private static final String ENTITY_COMMAND_EVENT_PREFIX = "[ENTCMD]";
    private static final String GENERIC_SUFFIX = "(Generic)";
    private static final String DESTROYING_SUFFIX = "(destroying)";
    private static final String NETWORK_GROUP_NULL_SUFFIX = "network group to null";

    @Override
    protected Function<RustRconResponse, Optional<OxidePluginEvent>> eventParser() {
        return payload -> {
            final var message = payload.getMessage();
            final var pluginName = message.substring(message.indexOf('[') + 1, message.indexOf(']')).trim();
            final var pluginMessage = message.substring(pluginName.length() + 2).trim();

            if (pluginName.isEmpty() || pluginMessage.isEmpty()) {
                return Optional.empty();
            }

            final var oxidePluginEvent = new OxidePluginEvent(pluginName, pluginMessage);
            return Optional.of(oxidePluginEvent);
        };
    }

    @Override
    public boolean supports(@NonNull RustRconResponse payload) {
        final var message = payload.getMessage();

        if (payload.getIdentifier() > 1) {
            return false;
        }

        if (message.isBlank()) {
            return false;
        }

        if (message.endsWith(GENERIC_SUFFIX) || message.endsWith(DESTROYING_SUFFIX) || message.endsWith(NETWORK_GROUP_NULL_SUFFIX)) {
            return false;
        }

        if (message.matches(EVENT_PATTERN)) {
            final var prefix = message.substring(message.indexOf('['), message.indexOf(']') + 1);
            return !prefix.equals(NATIVE_EVENT_PREFIX)
                    && !prefix.equals(CHAT_EVENT_PREFIX)
                    && !prefix.equals(TEAM_CHAT_EVENT_PREFIX)
                    && !prefix.equals(ENTITY_COMMAND_EVENT_PREFIX);
        }

        return false;
    }

    @Override
    public Class<OxidePluginEvent> eventClass() {
        return OxidePluginEvent.class;
    }
}
