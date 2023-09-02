package io.graversen.rust.rcon.event.player;

import io.graversen.rust.rcon.TestRustRconResponse;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlayerMiniCopterCrashedEventParserTest {
    private final PlayerMiniCopterCrashedEventParser playerMiniCopterCrashedEventParser = new PlayerMiniCopterCrashedEventParser();

    @ParameterizedTest
    @CsvFileSource(resources = "PlayerMiniCopterCrashedEvents_Verification.txt", numLinesToSkip = 1, delimiter = ';')
    void parseEvent_verification(String payload, String steamId, String playerName) {
        final var event = playerMiniCopterCrashedEventParser.parseEvent(new TestRustRconResponse(payload));
        assertTrue(event.isPresent());
        assertEquals(steamId, event.get().getSteamId().get());
        assertEquals(playerName, event.get().getPlayerName().get());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "PlayerMiniCopterCrashedEvents_Verification.txt", numLinesToSkip = 1, delimiter = ';')
    void supports_verification(String payload) {
        final var supports = playerMiniCopterCrashedEventParser.supports(new TestRustRconResponse(payload));
        assertTrue(supports);
    }
}