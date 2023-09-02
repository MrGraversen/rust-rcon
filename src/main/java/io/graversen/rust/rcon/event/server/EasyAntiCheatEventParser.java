package io.graversen.rust.rcon.event.server;

import io.graversen.rust.rcon.RustRconResponse;
import io.graversen.rust.rcon.event.BaseRustEventParser;
import io.graversen.rust.rcon.protocol.util.PlayerName;
import io.graversen.rust.rcon.protocol.util.SteamId64;
import lombok.NonNull;

import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;

public class EasyAntiCheatEventParser extends BaseRustEventParser<EasyAntiCheatEvent> {
    private static final Pattern EAC_LOG_PATTERN = Pattern.compile("\\[EAC\\] Kicking (\\d+) / (.*?) \\((.*?)\\)");

    @Override
    public boolean supports(@NonNull RustRconResponse payload) {
        return payload.getMessage().startsWith("[EAC] ");
    }

    @Override
    public Class<EasyAntiCheatEvent> eventClass() {
        return EasyAntiCheatEvent.class;
    }

    @Override
    protected Function<RustRconResponse, Optional<EasyAntiCheatEvent>> eventParser() {
        return rconResponse -> {
            final var message = rconResponse.getMessage();
            final var matcher = EAC_LOG_PATTERN.matcher(message);

            if (matcher.matches()) {
                final var steamId64 = matcher.group(1);
                final var playerName = matcher.group(2);
                final var reason = matcher.group(3);
                final var event = new EasyAntiCheatEvent(
                        rconResponse.getServer(),
                        new SteamId64(steamId64),
                        new PlayerName(playerName),
                        reason
                );
                return Optional.of(event);
            } else {
                return Optional.empty();
            }
        };
    }
}
