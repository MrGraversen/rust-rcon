package io.graversen.rust.rcon.test;

import io.graversen.rust.rcon.events.IEventParser;
import io.graversen.rust.rcon.events.implementation.PlayerSpawnedEventParser;
import io.graversen.rust.rcon.events.types.player.PlayerSpawnedEvent;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestParsePlayerSpawnedEvent extends BaseDefaultParserTest
{
    private static final String[] messages = {
            "DarkDouchebag[1014803/76561198046357656] has entered the game",
            "Doctor Delete[36356/76561197979952036] has entered the game"
    };

    private final IEventParser<PlayerSpawnedEvent> eventParser = new PlayerSpawnedEventParser();

    @Test
    void test_validation()
    {
        Arrays.stream(messages).map(eventParser.parseEvent()).forEach(event -> assertTrue(event.isPresent()));
    }

    @Test
    void test_findCorrectInfo_1()
    {
        final Optional<PlayerSpawnedEvent> event = eventParser.parseEvent().apply(messages[0]);

        assertTrue(event.isPresent());
        assertEquals("DarkDouchebag", event.get().getPlayerName());
        assertEquals("76561198046357656", event.get().getSteamId64());
    }
}
