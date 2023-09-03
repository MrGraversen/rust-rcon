package io.graversen.rust.rcon.event.server;

import io.graversen.rust.rcon.TestRustRconResponse;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import static org.junit.jupiter.api.Assertions.*;

class EntityCommandEventParserTest {
    private final EntityCommandEventParser entityCommandEventParser = new EntityCommandEventParser();

    @ParameterizedTest
    @CsvFileSource(resources = "EntityCommandEvents_Verification.txt", numLinesToSkip = 1)
    void parseEvent_verification(String payload, String command) {
        final var event = entityCommandEventParser.parseEvent(new TestRustRconResponse(payload));
        assertTrue(event.isPresent());
        assertEquals(command, event.get().getCommand());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "EntityCommandEvents_Verification.txt", numLinesToSkip = 1)
    void supports_verification(String payload) {
        final var supports = entityCommandEventParser.supports(new TestRustRconResponse(payload));
        assertTrue(supports);
    }
}