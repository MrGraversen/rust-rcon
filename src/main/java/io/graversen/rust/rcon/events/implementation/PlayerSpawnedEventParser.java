package io.graversen.rust.rcon.events.implementation;

import io.graversen.rust.rcon.events.IEventParser;
import io.graversen.rust.rcon.events.types.player.PlayerSpawnedEvent;
import io.graversen.rust.rcon.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;

public class PlayerSpawnedEventParser implements IEventParser<PlayerSpawnedEvent>
{
    @Override
    public Function<String, Optional<PlayerSpawnedEvent>> parseEvent()
    {
        return rconMessage ->
        {
            final Matcher matcherSteamId = Utils.squareBracketInsideMatcher.matcher(rconMessage);

            List<String> matchingStrings = new ArrayList<>();
            while (matcherSteamId.find())
            {
                matchingStrings.add(matcherSteamId.group(1));
            }

            final String steamId64 = matchingStrings.get(matchingStrings.size() - 1).split("/")[1];
            final String playerName = rconMessage.split("\\[")[0].trim();

            return Optional.of(new PlayerSpawnedEvent(playerName, steamId64));
        };
    }
}
