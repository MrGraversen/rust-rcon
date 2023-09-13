package io.graversen.rust.rcon.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RustUtilsTest {
    @Test
    void parseDateTime() {
        final var parsedDateTime = RustUtils.parseRustDateTime("09/07/2023 17:52:24");
        assertNotNull(parsedDateTime);
        assertEquals(CommonUtils.utc(), parsedDateTime.getZone());
        assertEquals("2023-09-07T17:52:24Z[UTC]", parsedDateTime.toString());
    }
}