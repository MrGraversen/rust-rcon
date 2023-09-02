package io.graversen.rust.rcon.event.oxide;

import io.graversen.rust.rcon.TestRustRconResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OxidePluginEventParserTest {
    private final OxidePluginEventParser oxidePluginEventParser = new OxidePluginEventParser();

    @Test
    void parseEvent_smoothRestarter() {
        final var message = "[SmoothRestarter] Current Oxide.Rust version is up-to-date, scheduling check after 600 seconds...";
        final var oxidePluginEvent = oxidePluginEventParser.eventParser().apply(new TestRustRconResponse(message));
        assertNotNull(oxidePluginEvent);
        assertTrue(oxidePluginEvent.isPresent());
        assertEquals("SmoothRestarter", oxidePluginEvent.get().getPluginName());
        assertEquals("Current Oxide.Rust version is up-to-date, scheduling check after 600 seconds...", oxidePluginEvent.get().getMessage());
    }

    @Test
    void parseEvent_undertaker() {
        final var message = "[Undertaker (Ownzone)] {\"victim\":\"Boar\",\"killer\":\"Сэр Ланселап\",\"bodypart\":\"Body\",\"distance\":\"1.3 meters\",\"hp\":\"94.7\",\"weapon\":\"Waterpipe Shotgun\",\"attachments\":\"\",\"killerId\":\"76561198201020867\",\"victimId\":null,\"damageType\":\"Bullet\",\"killerEntityType\":\"Player\",\"victimEntityType\":\"Animal\"}";
        final var oxidePluginEvent = oxidePluginEventParser.eventParser().apply(new TestRustRconResponse(message));
        assertNotNull(oxidePluginEvent);
        assertTrue(oxidePluginEvent.isPresent());
        assertEquals("Undertaker (Ownzone)", oxidePluginEvent.get().getPluginName());
        assertEquals("{\"victim\":\"Boar\",\"killer\":\"Сэр Ланселап\",\"bodypart\":\"Body\",\"distance\":\"1.3 meters\",\"hp\":\"94.7\",\"weapon\":\"Waterpipe Shotgun\",\"attachments\":\"\",\"killerId\":\"76561198201020867\",\"victimId\":null,\"damageType\":\"Bullet\",\"killerEntityType\":\"Player\",\"victimEntityType\":\"Animal\"}", oxidePluginEvent.get().getMessage());
    }

    @Test
    void supports_oxidePluginEvent() {
        final var supports = oxidePluginEventParser.supports(new TestRustRconResponse("[SmoothRestarter] Fetching latest Oxide.Rust version..."));
        assertTrue(supports);
    }

    @Test
    void supports_nativeEvent() {
        final var supports = oxidePluginEventParser.supports(new TestRustRconResponse("[event] assets/prefabs/npc/patrol helicopter/patrolhelicopter.prefab"));
        assertFalse(supports);

    }
}