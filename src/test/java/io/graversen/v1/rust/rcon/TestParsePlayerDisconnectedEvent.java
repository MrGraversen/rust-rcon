package io.graversen.v1.rust.rcon;

import io.graversen.v1.rust.rcon.events.IEventParser;
import io.graversen.v1.rust.rcon.events.implementation.PlayerDisconnectedEventParser;
import io.graversen.v1.rust.rcon.events.types.player.PlayerDisconnectedEvent;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestParsePlayerDisconnectedEvent extends BaseDefaultParserTest
{
    private static final String[] messages = {
            "82.102.20.168:53998/76561197979952036/Doctor Delete disconnecting: closing",
            "82.102.20.179:61714/76561197979952036/Pope of the Nope disconnecting: closing",
            "130.225.244.179:64745/76561198046357656/DarkDouchebag disconnecting: closing",
            "92.171.193.37:52278/76561198063601715/Orion (EN/FR/ES) disconnecting: closing"
    };

    private final IEventParser<PlayerDisconnectedEvent> eventParser = new PlayerDisconnectedEventParser();

    @Test
    void test_validation()
    {
        Arrays.stream(messages).map(eventParser.parseEvent()).forEach(event -> assertTrue(event.isPresent()));
    }

    @Test
    void test_findCorrectInfo_1()
    {
        final Optional<PlayerDisconnectedEvent> event = eventParser.parseEvent().apply(messages[0]);

        assertTrue(event.isPresent());
        assertEquals("Doctor Delete", event.get().getPlayerName());
        assertEquals("closing", event.get().getReason());
        assertEquals("76561197979952036", event.get().getSteamId64());
        assertEquals("82.102.20.168:53998", event.get().getConnectionTuple());
    }

    @Test
    void test_findCorrectInfo_2()
    {
        final Optional<PlayerDisconnectedEvent> event = eventParser.parseEvent().apply(messages[3]);

        assertTrue(event.isPresent());
        assertEquals("Orion (EN/FR/ES)", event.get().getPlayerName());
        assertEquals("closing", event.get().getReason());
        assertEquals("76561198063601715", event.get().getSteamId64());
        assertEquals("92.171.193.37:52278", event.get().getConnectionTuple());
    }
}
