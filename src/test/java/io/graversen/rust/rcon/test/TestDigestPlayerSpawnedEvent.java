package io.graversen.rust.rcon.test;

import io.graversen.rust.rcon.ConsoleDigests;
import io.graversen.rust.rcon.events.PlayerSpawnedEvent;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestDigestPlayerSpawnedEvent extends BaseDigesterTest
{
    private final String[] playerSpawnedMessages = {
            "DarkDouchebag[1014803/76561198046357656] has entered the game"
    };

    @Test
    void test_validation()
    {
        Arrays.stream(playerSpawnedMessages).forEach(s -> consoleMessageDigester.validateEvent(s, ConsoleDigests.PLAYER_SPAWNED));
    }

    @Test
    void test_findCorrectInfo_1()
    {
        final PlayerSpawnedEvent playerSpawnedEvent = consoleMessageDigester.digestPlayerSpawnedEvent(playerSpawnedMessages[0]);

        assertEquals("DarkDouchebag", playerSpawnedEvent.getPlayerName());
        assertEquals("76561198046357656", playerSpawnedEvent.getSteamId64());
    }
}
