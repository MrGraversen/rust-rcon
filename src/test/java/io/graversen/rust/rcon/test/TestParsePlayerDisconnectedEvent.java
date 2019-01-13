package io.graversen.rust.rcon.test;

import io.graversen.rust.rcon.RconMessages;
import io.graversen.rust.rcon.events.types.player.PlayerDisconnectedEvent;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestParsePlayerDisconnectedEvent extends BaseDefaultParserTest
{
    private static final String[] disconnectMessages = {
            "82.102.20.168:53998/76561197979952036/Doctor Delete disconnecting: closing",
            "82.102.20.179:61714/76561197979952036/Pope of the Nope disconnecting: closing",
            "130.225.244.179:64745/76561198046357656/DarkDouchebag disconnecting: closing",
            "92.171.193.37:52278/76561198063601715/Orion (EN/FR/ES) disconnecting: closing"
    };

    @Test
    void test_validation()
    {
        Arrays.stream(disconnectMessages).forEach(s -> defaultConsoleParser.validateEvent(s, RconMessages.PLAYER_DISCONNECTED));
    }

    @Test
    void test_findCorrectInfo_1()
    {
        final PlayerDisconnectedEvent playerDisconnectedEvent = defaultConsoleParser.parsePlayerDisconnectedEvent(disconnectMessages[0]);

        assertEquals("Doctor Delete", playerDisconnectedEvent.getPlayerName());
        assertEquals("closing", playerDisconnectedEvent.getReason());
        assertEquals("76561197979952036", playerDisconnectedEvent.getSteamId64());
        assertEquals("82.102.20.168:53998", playerDisconnectedEvent.getConnectionTuple());
    }
}