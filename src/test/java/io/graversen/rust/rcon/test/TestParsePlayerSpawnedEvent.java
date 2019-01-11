package io.graversen.rust.rcon.test;

import io.graversen.rust.rcon.RconMessages;
import io.graversen.rust.rcon.events.types.player.PlayerSpawnedEvent;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestParsePlayerSpawnedEvent extends BaseDefaultParserTest
{
    private static final String[] playerSpawnedMessages = {
            "DarkDouchebag[1014803/76561198046357656] has entered the game"
    };

    @Test
    void test_validation()
    {
        Arrays.stream(playerSpawnedMessages).forEach(s -> defaultConsoleParser.validateEvent(s, RconMessages.PLAYER_SPAWNED));
    }

    @Test
    void test_findCorrectInfo_1()
    {
        final PlayerSpawnedEvent playerSpawnedEvent = defaultConsoleParser.parserPlayerSpawnedEvent(playerSpawnedMessages[0]);

        assertEquals("DarkDouchebag", playerSpawnedEvent.getPlayerName());
        assertEquals("76561198046357656", playerSpawnedEvent.getSteamId64());
    }
}
