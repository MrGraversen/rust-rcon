package io.graversen.rust.rcon.event.server;

import io.graversen.rust.rcon.TestRustRconResponse;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EasyAntiCheatEventParserTest {
    private final EasyAntiCheatEventParser easyAntiCheatEventParser = new EasyAntiCheatEventParser();

    @ParameterizedTest
    @CsvFileSource(resources = "EasyAntiCheatEvents_Verification.txt", numLinesToSkip = 1)
    void parseEvent_verification(String payload, String steamId, String playerName, String reason) {
        final var event = easyAntiCheatEventParser.parseEvent(new TestRustRconResponse(payload));
        assertTrue(event.isPresent());
        assertEquals(steamId, event.get().getSteamId().get());
        assertEquals(playerName, event.get().getPlayerName().get());
        assertEquals(reason, event.get().getReason());

    }

    @ParameterizedTest
    @CsvFileSource(resources = "EasyAntiCheatEvents_Verification.txt", numLinesToSkip = 1)
    void supports_verification(String payload) {
        final var supports = easyAntiCheatEventParser.supports(new TestRustRconResponse(payload));
        assertTrue(supports);
    }
}