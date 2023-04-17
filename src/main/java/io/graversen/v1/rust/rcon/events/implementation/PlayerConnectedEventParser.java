package io.graversen.v1.rust.rcon.events.implementation;

import io.graversen.v1.rust.rcon.events.IEventParser;
import io.graversen.v1.rust.rcon.events.types.player.PlayerConnectedEvent;
import io.graversen.v1.rust.rcon.util.Utils;

import java.util.Optional;
import java.util.function.Function;

public class PlayerConnectedEventParser implements IEventParser<PlayerConnectedEvent>
{
    @Override
    public Function<String, Optional<PlayerConnectedEvent>> parseEvent()
    {
        return rconMessage ->
        {
            final String[] splitInput = rconMessage.split("/");

            final String connectionTuple = splitInput[0];
            final String steamId64 = splitInput[1];

            final int osDescriptorFromIndex = rconMessage.lastIndexOf('[') + 1;
            final int osDescriptorToIndex = rconMessage.lastIndexOf('/');
            final String osDescriptor = rconMessage.substring(osDescriptorFromIndex, osDescriptorToIndex);

            final int playerNameFromIndex = Utils.nthIndexOf(rconMessage, '/', 2) + 1;
            final int playerNameToIndex = rconMessage.lastIndexOf("joined");
            final String playerName = rconMessage.substring(playerNameFromIndex, playerNameToIndex).trim();

            return Optional.of(new PlayerConnectedEvent(connectionTuple, osDescriptor, steamId64, playerName));
        };
    }
}
