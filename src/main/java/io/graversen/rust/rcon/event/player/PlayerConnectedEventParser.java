package io.graversen.rust.rcon.event.player;

import io.graversen.rust.rcon.RustRconResponse;
import io.graversen.rust.rcon.event.BaseRustEventParser;
import io.graversen.rust.rcon.protocol.util.OperatingSystems;
import io.graversen.rust.rcon.protocol.util.PlayerName;
import io.graversen.rust.rcon.protocol.util.SteamId64;
import io.graversen.rust.rcon.util.CommonUtils;
import lombok.NonNull;

import java.util.Optional;
import java.util.function.Function;

public class PlayerConnectedEventParser extends BaseRustEventParser<PlayerConnectedEvent> {
    @Override
    protected Function<RustRconResponse, Optional<PlayerConnectedEvent>> eventParser() {
        return payload -> {
            final var message = payload.getMessage();
            final var splitInput = message.split(delimiter());

            final var connectionTuple = splitInput[0];
            final var ipAddress = connectionTuple.split(":")[0];
            final var steamId64 = splitInput[1];

            final int osDescriptorFromIndex = message.lastIndexOf('[') + 1;
            final int osDescriptorToIndex = message.lastIndexOf(delimiter());
            final var operatingSystemString = message.substring(osDescriptorFromIndex, osDescriptorToIndex);

            final int playerNameFromIndex = CommonUtils.nthIndexOf(message, delimiter().charAt(0), 2) + 1;
            final int playerNameToIndex = message.lastIndexOf("joined");
            final var playerName = message.substring(playerNameFromIndex, playerNameToIndex).trim();

            final var operatingSystem = OperatingSystems.parse(operatingSystemString);

            final var playerConnectedEvent = new PlayerConnectedEvent(
                    new SteamId64(steamId64),
                    new PlayerName(playerName),
                    operatingSystem,
                    ipAddress
            );

            return Optional.of(playerConnectedEvent);
        };
    }

    @Override
    public boolean supports(@NonNull RustRconResponse payload) {
        return payload.getMessage().contains(" joined ") && payload.getMessage().split(delimiter()).length >= 4;
    }

    @Override
    public Class<PlayerConnectedEvent> eventClass() {
        return PlayerConnectedEvent.class;
    }

    protected String delimiter() {
        return "/";
    }
}
