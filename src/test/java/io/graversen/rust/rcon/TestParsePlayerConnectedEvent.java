package io.graversen.rust.rcon;

import io.graversen.rust.rcon.events.IEventParser;
import io.graversen.rust.rcon.events.implementation.PlayerConnectedEventParser;
import io.graversen.rust.rcon.events.types.player.PlayerConnectedEvent;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestParsePlayerConnectedEvent extends BaseDefaultParserTest
{
    private static final String[] messages = {
            "82.102.20.179:52298/76561197979952036/Pope of the Nope joined [windows/76561197979952036]",
            "79.193.40.58:55162/76561198845816557/m_7o7 joined [linux/76561198845816557]",
            "92.171.193.37:52278/76561198063601715/Orion (EN/FR/ES) joined [windows/76561198063601715]",
            "62.203.160.70:61437/76561198896647223/dkbarragues22 joined [osx/76561198896647223]"
    };

    private final IEventParser<PlayerConnectedEvent> eventParser = new PlayerConnectedEventParser();

    @Test
    void test_validation()
    {
        Arrays.stream(messages).map(eventParser.parseEvent()).forEach(event -> assertTrue(event.isPresent()));
    }

    @Test
    void test_findCorrectInfo_1()
    {
        final Optional<PlayerConnectedEvent> event = eventParser.parseEvent().apply(messages[0]);

        assertTrue(event.isPresent());
        assertEquals("Pope of the Nope", event.get().getPlayerName());
        assertEquals("windows", event.get().getOsDescriptor());
        assertEquals("76561197979952036", event.get().getSteamId64());
        assertEquals("82.102.20.179:52298", event.get().getConnectionTuple());
    }

    @Test
    void test_findCorrectInfo_2()
    {
        final Optional<PlayerConnectedEvent> event = eventParser.parseEvent().apply(messages[2]);

        assertTrue(event.isPresent());
        assertEquals("Orion (EN/FR/ES)", event.get().getPlayerName());
        assertEquals("windows", event.get().getOsDescriptor());
        assertEquals("76561198063601715", event.get().getSteamId64());
        assertEquals("92.171.193.37:52278", event.get().getConnectionTuple());
    }

    @Test
    void test_findCorrectInfo_3()
    {
        final Optional<PlayerConnectedEvent> event = eventParser.parseEvent().apply(messages[3]);

        assertTrue(event.isPresent());
        assertEquals("dkbarragues22", event.get().getPlayerName());
        assertEquals("osx", event.get().getOsDescriptor());
        assertEquals("76561198896647223", event.get().getSteamId64());
        assertEquals("62.203.160.70:61437", event.get().getConnectionTuple());
    }
}
