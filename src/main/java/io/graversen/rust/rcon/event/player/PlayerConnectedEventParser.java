package io.graversen.rust.rcon.event.player;

import io.graversen.rust.rcon.RustRconResponse;
import io.graversen.rust.rcon.event.BaseRustEventParser;
import io.graversen.rust.rcon.protocol.util.OperatingSystems;
import io.graversen.rust.rcon.protocol.util.PlayerName;
import io.graversen.rust.rcon.protocol.util.SteamId64;
import lombok.NonNull;

import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;

public class PlayerConnectedEventParser extends BaseRustEventParser<PlayerConnectedEvent> {
    private static final Pattern PATTERN_ONE = Pattern.compile("^([\\d\\.]+):\\d+/(\\d+)/(.+?) joined \\[(.+?)/");
    private static final Pattern PATTERN_TWO = Pattern.compile("^(.+?) with steamid (\\d+) joined from ip ([\\d\\.]+):\\d+$");

    @Override
    protected Function<RustRconResponse, Optional<PlayerConnectedEvent>> eventParser() {
        return payload -> {
            final var message = payload.getMessage();
            final var matcherOne = PATTERN_ONE.matcher(message);
            final var matcherTwo = PATTERN_TWO.matcher(message);

            if (matcherOne.find()) {
                final var ipAddress = matcherOne.group(1);
                final var steamId64 = matcherOne.group(2);
                final var playerName = matcherOne.group(3);
                final var operatingSystemString = matcherOne.group(4);
                final var operatingSystem = OperatingSystems.parse(operatingSystemString);

                return Optional.of(new PlayerConnectedEvent(
                        new SteamId64(steamId64),
                        new PlayerName(playerName),
                        operatingSystem,
                        ipAddress
                ));
            } else if (matcherTwo.find()) {
                final var playerName = matcherTwo.group(1);
                final var steamId64 = matcherTwo.group(2);
                final var ipAddress = matcherTwo.group(3);

                return Optional.of(new PlayerConnectedEvent(
                        new SteamId64(steamId64),
                        new PlayerName(playerName),
                        OperatingSystems.UNKNOWN,
                        ipAddress
                ));
            }

            return Optional.empty();
        };
    }

    @Override
    public boolean supports(@NonNull RustRconResponse payload) {
        final var message = payload.getMessage();
        return PATTERN_ONE.matcher(message).find() || PATTERN_TWO.matcher(message).find();
    }

    @Override
    public Class<PlayerConnectedEvent> eventClass() {
        return PlayerConnectedEvent.class;
    }
}
