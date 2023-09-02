package io.graversen.rust.rcon.event.player;

import io.graversen.rust.rcon.RustRconResponse;
import io.graversen.rust.rcon.event.BaseRustEventParser;
import io.graversen.rust.rcon.protocol.util.SteamId64;
import io.graversen.rust.rcon.protocol.util.SuicideCauses;
import lombok.NonNull;

import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public class PlayerSuicideEventParser extends BaseRustEventParser<PlayerSuicideEvent> {
    private static final Set<String> VALID_SUFFIXES = Set.of(
            "] was suicide by Suicide",
            "] was suicide by Blunt",
            "] was suicide by Stab",
            "] was suicide by Explosion",
            "] was killed by fall!",
            "] was killed by Drowned",
            "] was killed by Hunger",
            "] was killed by Thirst",
            "] was suicide by Heat",
            "] was killed by Cold",
            "] died (Fall)",
            "] died (Bleeding)"
    );

    @Override
    public boolean supports(@NonNull RustRconResponse payload) {
        return VALID_SUFFIXES.stream().anyMatch(suffix -> payload.getMessage().endsWith(suffix));
    }

    @Override
    protected Function<RustRconResponse, Optional<PlayerSuicideEvent>> eventParser() {
        return payload -> {
            final var message = payload.getMessage();

            final var steamIdStart = message.indexOf('/');
            final var steamId = new SteamId64(message.substring(steamIdStart + 1, steamIdStart + SteamId64.LENGTH + 1));

            final var playerNameEnd = message.indexOf('[');
            final var playerName = message.substring(0, playerNameEnd);

            final var steamIdEnd = message.lastIndexOf(']');
            final var detailMessage = message.substring(steamIdEnd + 1).trim();

            switch (detailMessage) {
                case "was suicide by Suicide":
                    return Optional.of(new PlayerSuicideEvent(steamId, SuicideCauses.SUICIDE));
                case "was suicide by Blunt":
                    return Optional.of(new PlayerSuicideEvent(steamId, SuicideCauses.BLUNT));
                case "was suicide by Stab":
                    return Optional.of(new PlayerSuicideEvent(steamId, SuicideCauses.STAB));
                case "was suicide by Explosion":
                    return Optional.of(new PlayerSuicideEvent(steamId, SuicideCauses.EXPLOSION));
                case "was killed by fall!":
                case "died (Fall)":
                    return Optional.of(new PlayerSuicideEvent(steamId, SuicideCauses.FALL));
                case "was killed by Drowned":
                    return Optional.of(new PlayerSuicideEvent(steamId, SuicideCauses.DROWN));
                case "was killed by Hunger":
                    return Optional.of(new PlayerSuicideEvent(steamId, SuicideCauses.HUNGER));
                case "was killed by Thirst":
                    return Optional.of(new PlayerSuicideEvent(steamId, SuicideCauses.THIRST));
                case "was suicide by Heat":
                    return Optional.of(new PlayerSuicideEvent(steamId, SuicideCauses.HEAT));
                case "was killed by Cold":
                    return Optional.of(new PlayerSuicideEvent(steamId, SuicideCauses.COLD));
                case "died (Bleeding)":
                    return Optional.of(new PlayerSuicideEvent(steamId, SuicideCauses.BLEEDING));
            }

            return Optional.empty();
        };
    }

    @Override
    public Class<PlayerSuicideEvent> eventClass() {
        return PlayerSuicideEvent.class;
    }
}
