package io.graversen.rust.rcon.events.implementation;

import io.graversen.rust.rcon.events.IEventParser;
import io.graversen.rust.rcon.events.types.player.PlayerDisconnectedEvent;

import java.util.Optional;
import java.util.function.Function;

public class PlayerDisconnectedEventParser implements IEventParser<PlayerDisconnectedEvent>
{
    @Override
    public Function<String, Optional<PlayerDisconnectedEvent>> parseEvent()
    {
        return rconMessage ->
        {
            final String[] splitInput = rconMessage.split("/");
            final String[] splitInputLastElement = splitInput[2].split("disconnecting:");

            final String ipAddress = splitInput[0];
            final String steamId64 = splitInput[1];
            final String playerName = splitInputLastElement[0].trim();
            final String reason = splitInputLastElement[1].trim();

            return Optional.of(new PlayerDisconnectedEvent(ipAddress, steamId64, playerName, reason));
        };
    }
}
