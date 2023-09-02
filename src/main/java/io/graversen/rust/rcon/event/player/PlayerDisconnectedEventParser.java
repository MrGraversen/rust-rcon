package io.graversen.rust.rcon.event.player;

import io.graversen.rust.rcon.RustRconResponse;
import io.graversen.rust.rcon.event.BaseRustEventParser;
import io.graversen.rust.rcon.protocol.util.SteamId64;
import io.graversen.rust.rcon.util.CommonUtils;
import lombok.NonNull;

import java.util.Optional;
import java.util.function.Function;

public class PlayerDisconnectedEventParser extends BaseRustEventParser<PlayerDisconnectedEvent> {
    @Override
    protected Function<RustRconResponse, Optional<PlayerDisconnectedEvent>> eventParser() {
        return payload -> {
            final var message = payload.getMessage();
            final String[] splitInput = message.split("/");
            final String[] splitInputLastElement = CommonUtils.partialJoin("/", splitInput, 2, splitInput.length).split("disconnecting:");

            final String steamId64 = splitInput[1];
            final String reason = splitInputLastElement[1].trim();

            final var playerDisconnectedEvent = new PlayerDisconnectedEvent(
                    new SteamId64(steamId64),
                    reason
            );

            return Optional.of(playerDisconnectedEvent);
        };
    }

    @Override
    public boolean supports(@NonNull RustRconResponse payload) {
        return payload.getMessage().contains("disconnecting:");
    }

    @Override
    public Class<PlayerDisconnectedEvent> eventClass() {
        return PlayerDisconnectedEvent.class;
    }
}
