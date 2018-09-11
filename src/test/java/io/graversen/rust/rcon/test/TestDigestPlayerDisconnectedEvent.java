package io.graversen.rust.rcon.test;

import io.graversen.rust.rcon.ConsoleDigests;
import io.graversen.rust.rcon.events.PlayerDisconnectedEvent;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class TestDigestPlayerDisconnectedEvent extends BaseDigesterTest
{
    private final String[] disconnectMessages = {
            "82.102.20.168:53998/76561197979952036/Doctor Delete disconnecting: closing",
            "82.102.20.179:61714/76561197979952036/Pope of the Nope disconnecting: closing",
            "130.225.244.179:64745/76561198046357656/DarkDouchebag disconnecting: closing"
    };

    @Test
    void test_validation()
    {
        Arrays.stream(disconnectMessages).forEach(s -> consoleMessageDigester.validateEvent(s, ConsoleDigests.PLAYER_DISCONNECTED));
    }

    @Test
    void test_findCorrectInfo_1()
    {
        final PlayerDisconnectedEvent playerDisconnectedEvent = consoleMessageDigester.digestPlayerDisconnectedEvent(disconnectMessages[0]);

        assertEquals("Doctor Delete", playerDisconnectedEvent.getPlayerName());
        assertEquals("closing", playerDisconnectedEvent.getReason());
        assertEquals("76561197979952036", playerDisconnectedEvent.getSteamId64());
        assertEquals("82.102.20.168:53998", playerDisconnectedEvent.getConnectionTuple());
    }
}
