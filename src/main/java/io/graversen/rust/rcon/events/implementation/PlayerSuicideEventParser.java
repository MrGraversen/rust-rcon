package io.graversen.rust.rcon.events.implementation;

import io.graversen.rust.rcon.events.IEventParser;
import io.graversen.rust.rcon.events.types.player.PlayerSuicideEvent;
import io.graversen.rust.rcon.objects.util.SuicideCauses;

import java.util.Optional;
import java.util.function.Function;

public class PlayerSuicideEventParser implements IEventParser<PlayerSuicideEvent>
{
    private static final int STEAM_ID_LENGTH = "76561197979952036".length();

    @Override
    public Function<String, Optional<PlayerSuicideEvent>> parseEvent()
    {
        return rconMessage ->
        {
            final int steamIdStart = rconMessage.indexOf('/');
            final String steamId = rconMessage.substring(steamIdStart + 1, steamIdStart + STEAM_ID_LENGTH + 1);

            final int playerNameEnd = rconMessage.indexOf('[');
            final String playerName = rconMessage.substring(0, playerNameEnd);

            final int steamIdEnd = rconMessage.lastIndexOf(']');
            final String detailMessage = rconMessage.substring(steamIdEnd + 1).trim();

            switch (detailMessage)
            {
                case "was suicide by Suicide":
                    return Optional.of(new PlayerSuicideEvent(playerName, steamId, SuicideCauses.SUICIDE));
                case "was suicide by Blunt":
                    return Optional.of(new PlayerSuicideEvent(playerName, steamId, SuicideCauses.BLUNT));
                case "was suicide by Stab":
                    return Optional.of(new PlayerSuicideEvent(playerName, steamId, SuicideCauses.STAB));
                case "was suicide by Explosion":
                    return Optional.of(new PlayerSuicideEvent(playerName, steamId, SuicideCauses.EXPLOSION));
                case "was killed by fall!":
                case "died (Fall)":
                    return Optional.of(new PlayerSuicideEvent(playerName, steamId, SuicideCauses.FALL));
                case "was killed by Drowned":
                    return Optional.of(new PlayerSuicideEvent(playerName, steamId, SuicideCauses.DROWN));
                case "was killed by Hunger":
                    return Optional.of(new PlayerSuicideEvent(playerName, steamId, SuicideCauses.HUNGER));
                case "was killed by Thirst":
                    return Optional.of(new PlayerSuicideEvent(playerName, steamId, SuicideCauses.THIRST));
                case "was suicide by Heat":
                    return Optional.of(new PlayerSuicideEvent(playerName, steamId, SuicideCauses.HEAT));
                case "was killed by Cold":
                    return Optional.of(new PlayerSuicideEvent(playerName, steamId, SuicideCauses.COLD));
                case "died (Bleeding)":
                    return Optional.of(new PlayerSuicideEvent(playerName, steamId, SuicideCauses.BLEEDING));
            }

            return Optional.empty();
        };
    }
}
