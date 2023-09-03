package io.graversen.rust.rcon.event.player;

import io.graversen.rust.rcon.RustRconResponse;
import io.graversen.rust.rcon.event.BaseRustEventParser;
import io.graversen.rust.rcon.protocol.util.PlayerName;
import io.graversen.rust.rcon.protocol.util.SteamId64;
import io.graversen.rust.rcon.util.CommonUtils;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlayerChatEventParser extends BaseRustEventParser<PlayerChatEvent> {
    private final Pattern CHAT_PATTERN = Pattern.compile("\\[CHAT\\] (.+)\\[(\\d+)\\] : (.+)");

    @Override
    public boolean supports(@NonNull RustRconResponse payload) {
        return payload.getMessage().startsWith("[CHAT] ");
    }

    @Override
    public Class<PlayerChatEvent> eventClass() {
        return PlayerChatEvent.class;
    }

    @Override
    protected Function<RustRconResponse, Optional<PlayerChatEvent>> eventParser() {
        return rconResponse -> {
            final var message = rconResponse.getMessage();

            final var matcher = CHAT_PATTERN.matcher(message);

            if (matcher.find()) {
                final var playerName = matcher.group(1).trim();
                final var steamId64 = matcher.group(2).trim();
                final var chatMessage = matcher.group(3).trim();

                final var playerChatEvent = new PlayerChatEvent(
                        new SteamId64(steamId64),
                        PlayerName.ofNullable(playerName),
                        chatMessage
                );

                return Optional.of(playerChatEvent);
            }
            return Optional.empty();
        };
    }
}
