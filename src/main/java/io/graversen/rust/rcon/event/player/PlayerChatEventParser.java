package io.graversen.rust.rcon.event.player;

import io.graversen.rust.rcon.RustRconResponse;
import io.graversen.rust.rcon.event.BaseRustEventParser;
import io.graversen.rust.rcon.protocol.util.ChatChannels;
import io.graversen.rust.rcon.protocol.util.PlayerName;
import io.graversen.rust.rcon.protocol.util.SteamId64;
import lombok.NonNull;

import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;

public class PlayerChatEventParser extends BaseRustEventParser<PlayerChatEvent> {
    private final Pattern CHAT_PATTERN = Pattern.compile("\\[CHAT\\] (.+)\\[(\\d+)\\] : (.+)");
    private final Pattern TEAM_CHAT_PATTERN = Pattern.compile("\\[TEAM CHAT\\] (.+)\\[(\\d+)\\] : (.+)");

    @Override
    public boolean supports(@NonNull RustRconResponse payload) {
        return payload.getMessage().startsWith("[CHAT] ") || payload.getMessage().startsWith("[TEAM CHAT] ");
    }

    @Override
    public Class<PlayerChatEvent> eventClass() {
        return PlayerChatEvent.class;
    }

    @Override
    protected Function<RustRconResponse, Optional<PlayerChatEvent>> eventParser() {
        return rconResponse -> {
            final var message = rconResponse.getMessage();

            final var chatMatcher = CHAT_PATTERN.matcher(message);
            final var teamChatMatcher = TEAM_CHAT_PATTERN.matcher(message);

            if (chatMatcher.find()) {
                final var playerName = chatMatcher.group(1).trim();
                final var steamId64 = chatMatcher.group(2).trim();
                final var chatMessage = chatMatcher.group(3).trim();

                final var playerChatEvent = new PlayerChatEvent(
                        new SteamId64(steamId64),
                        PlayerName.ofNullable(playerName),
                        chatMessage,
                        ChatChannels.DEFAULT
                );

                return Optional.of(playerChatEvent);
            } else if (teamChatMatcher.find()) {
                final var playerName = teamChatMatcher.group(1).trim();
                final var steamId64 = teamChatMatcher.group(2).trim();
                final var chatMessage = teamChatMatcher.group(3).trim();

                final var playerChatEvent = new PlayerChatEvent(
                        new SteamId64(steamId64),
                        PlayerName.ofNullable(playerName),
                        chatMessage,
                        ChatChannels.TEAM
                );

                return Optional.of(playerChatEvent);
            }

            return Optional.empty();
        };
    }
}
