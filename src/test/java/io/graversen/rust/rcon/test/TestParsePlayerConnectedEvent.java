package io.graversen.rust.rcon.test;

import io.graversen.rust.rcon.RconMessages;
import io.graversen.rust.rcon.events.types.player.PlayerConnectedEvent;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestParsePlayerConnectedEvent extends BaseDefaultParserTest
{
    private static final String[] connectMessages = {
            "82.102.20.179:52298/76561197979952036/Pope of the Nope joined [windows/76561197979952036]",
            "79.193.40.58:55162/76561198845816557/m_7o7 joined [linux/76561198845816557]",
            "92.171.193.37:52278/76561198063601715/Orion (EN/FR/ES) joined [windows/76561198063601715]"
    };

    @Test
    void test_validation()
    {
        Arrays.stream(connectMessages).forEach(s -> defaultConsoleParser.validateEvent(s, RconMessages.PLAYER_CONNECTED));
    }

    @Test
    void test_findCorrectInfo_1()
    {
        final PlayerConnectedEvent playerConnectedEvent = defaultConsoleParser.parsePlayerConnectedEvent(connectMessages[0]);

        assertEquals("Pope of the Nope", playerConnectedEvent.getPlayerName());
        assertEquals("windows", playerConnectedEvent.getOsDescriptor());
        assertEquals("76561197979952036", playerConnectedEvent.getSteamId64());
        assertEquals("82.102.20.179:52298", playerConnectedEvent.getConnectionTuple());
    }

    @Test
    void test_findCorrectInfo_2()
    {
        final PlayerConnectedEvent playerConnectedEvent = defaultConsoleParser.parsePlayerConnectedEvent(connectMessages[2]);

        assertEquals("Orion (EN/FR/ES)", playerConnectedEvent.getPlayerName());
        assertEquals("windows", playerConnectedEvent.getOsDescriptor());
        assertEquals("76561198063601715", playerConnectedEvent.getSteamId64());
        assertEquals("92.171.193.37:52278", playerConnectedEvent.getConnectionTuple());
    }

}
