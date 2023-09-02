package io.graversen.rust.rcon.event.player;

import io.graversen.rust.rcon.TestRustRconResponse;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlayerChatEventParserTest {
    private final PlayerChatEventParser playerChatEventParser = new PlayerChatEventParser();

    @ParameterizedTest
    @CsvFileSource(resources = "PlayerChatEvents_Verification.txt", numLinesToSkip = 1, delimiter = ';')
    void parseEvent_verification(String payload, String steamId, String playerName, String message) {
        final var event = playerChatEventParser.parseEvent(new TestRustRconResponse(payload));
        assertTrue(event.isPresent());
        assertEquals(steamId, event.get().getSteamId().get());
        assertEquals(playerName, event.get().getPlayerName().get());
        assertEquals(message, event.get().getMessage());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "PlayerChatEvents_Verification.txt", numLinesToSkip = 1, delimiter = ';')
    void supports_verification(String payload) {
        final var supports = playerChatEventParser.supports(new TestRustRconResponse(payload));
        assertTrue(supports);
    }
}