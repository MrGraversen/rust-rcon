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

public class PlayerChatEventParser extends BaseRustEventParser<PlayerChatEvent> {
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

            final var chatMessageParts = message.split("\\s:\\s", 2);

            final var leftHandString = chatMessageParts[0];
            final var chatMessage = chatMessageParts[1].trim();

            final var matcherSteamId = CommonUtils.SQUARE_BRACKET_INSIDE_MATCHER.matcher(leftHandString);

            final var matchingStrings = new ArrayList<String>();
            while (matcherSteamId.find()) {
                matchingStrings.add(matcherSteamId.group(1));
            }

            final var steamId64 = matchingStrings.get(matchingStrings.size() - 1);
            final var matcherPlayerName = CommonUtils.SQUARE_BRACKET_OUTSIDE_MATCHER.matcher(leftHandString);

            final String playerName = matcherPlayerName.find()
                    ? matcherPlayerName.group(1).trim()
                    : null;

            final var playerChatEvent = new PlayerChatEvent(
                    new SteamId64(steamId64),
                    PlayerName.ofNullable(playerName),
                    chatMessage
            );

            return Optional.of(playerChatEvent);
        };
    }
}
