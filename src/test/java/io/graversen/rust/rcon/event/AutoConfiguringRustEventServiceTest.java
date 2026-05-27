package io.graversen.rust.rcon.event;

import com.google.common.eventbus.EventBus;
import io.graversen.rust.rcon.event.player.PlayerChatEvent;
import io.graversen.rust.rcon.event.player.PlayerConnectedEvent;
import io.graversen.rust.rcon.event.server.SaveEvent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AutoConfiguringRustEventServiceTest {
    @Test
    void capabilitiesExposeConfiguredRconParserEvents() {
        final var eventService = new AutoConfiguringRustEventService(new EventBus());

        eventService.configure();

        final var capabilities = eventService.capabilities();
        assertEquals(RustEventSourceStrategy.RCON, capabilities.getStrategy());
        assertTrue(capabilities.supports(PlayerChatEvent.class));
        assertTrue(capabilities.supports(PlayerConnectedEvent.class));
        assertTrue(capabilities.supports(SaveEvent.class));
    }
}
