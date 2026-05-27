package io.graversen.rust.rcon.event.umod;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import io.graversen.rust.rcon.TestRustRconResponse;
import io.graversen.rust.rcon.event.RustEventSourceStrategy;
import io.graversen.rust.rcon.event.player.PlayerChatEvent;
import io.graversen.rust.rcon.event.player.PlayerConnectedEvent;
import io.graversen.rust.rcon.event.player.PlayerDeathEvent;
import io.graversen.rust.rcon.event.player.PlayerDisconnectedEvent;
import io.graversen.rust.rcon.event.rcon.RconReceivedEvent;
import io.graversen.rust.rcon.protocol.util.ChatChannels;
import io.graversen.rust.rcon.protocol.util.CombatTypes;
import io.graversen.rust.rcon.protocol.util.DamageTypes;
import io.graversen.rust.rcon.protocol.util.OperatingSystems;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UmodBridgeRustEventServiceTest {
    @Test
    void capabilitiesExposeUmodBridgeEvents() {
        final var eventService = new UmodBridgeRustEventService(new EventBus());

        final var capabilities = eventService.capabilities();

        assertEquals(RustEventSourceStrategy.UMOD, capabilities.getStrategy());
        assertTrue(capabilities.supports(PlayerChatEvent.class));
        assertTrue(capabilities.supports(PlayerConnectedEvent.class));
        assertTrue(capabilities.supports(PlayerDeathEvent.class));
        assertTrue(capabilities.supports(PlayerDisconnectedEvent.class));
        assertTrue(capabilities.supports(UmodBridgeDiagnosticEvent.class));
    }

    @Test
    void emitsPlayerChatEventFromBridgeEnvelope() {
        final var eventBus = new EventBus();
        final var eventService = new UmodBridgeRustEventService(eventBus);
        final var subscriber = new ChatSubscriber();
        eventBus.register(subscriber);
        eventService.configure();

        eventBus.post(rconReceived("[rust-rcon] {\"schemaVersion\":1,\"eventType\":\"player.chat\",\"eventId\":\"evt-1\",\"timestamp\":\"2026-05-27T12:00:00Z\",\"payload\":{\"steamId\":\"76561197979952036\",\"playerName\":\"Doctor Delete\",\"message\":\"Hi from bridge\",\"chatChannel\":\"team\"}}"));

        assertEquals(1, subscriber.events.size());
        assertEquals("76561197979952036", subscriber.events.get(0).getSteamId().get());
        assertEquals("Doctor Delete", subscriber.events.get(0).getPlayerName().get());
        assertEquals("Hi from bridge", subscriber.events.get(0).getMessage());
        assertEquals(ChatChannels.TEAM, subscriber.events.get(0).getChatChannel());
    }

    @Test
    void ignoresNonBridgeRconMessages() {
        final var eventBus = new EventBus();
        final var eventService = new UmodBridgeRustEventService(eventBus);
        final var subscriber = new ChatSubscriber();
        eventBus.register(subscriber);
        eventService.configure();

        eventBus.post(rconReceived("[Global] Doctor Delete : Hi from vanilla logs"));

        assertTrue(subscriber.events.isEmpty());
    }

    @Test
    void emitsPlayerConnectedEventFromBridgeEnvelope() {
        final var eventBus = new EventBus();
        final var eventService = new UmodBridgeRustEventService(eventBus);
        final var subscriber = new ConnectedSubscriber();
        eventBus.register(subscriber);
        eventService.configure();

        eventBus.post(rconReceived("[rust-rcon] {\"schemaVersion\":1,\"eventType\":\"player.connected\",\"eventId\":\"evt-2\",\"timestamp\":\"2026-05-27T12:00:00Z\",\"payload\":{\"steamId\":\"76561197979952036\",\"playerName\":\"Doctor Delete\",\"ipAddress\":\"127.0.0.1:12345\"}}"));

        assertEquals(1, subscriber.events.size());
        assertEquals("76561197979952036", subscriber.events.get(0).getSteamId().get());
        assertEquals("Doctor Delete", subscriber.events.get(0).getPlayerName().get());
        assertEquals("127.0.0.1:12345", subscriber.events.get(0).getIpAddress());
        assertEquals(OperatingSystems.UNKNOWN, subscriber.events.get(0).getOperatingSystem());
    }

    @Test
    void emitsPlayerDisconnectedEventFromBridgeEnvelope() {
        final var eventBus = new EventBus();
        final var eventService = new UmodBridgeRustEventService(eventBus);
        final var subscriber = new DisconnectedSubscriber();
        eventBus.register(subscriber);
        eventService.configure();

        eventBus.post(rconReceived("[rust-rcon] {\"schemaVersion\":1,\"eventType\":\"player.disconnected\",\"eventId\":\"evt-3\",\"timestamp\":\"2026-05-27T12:00:00Z\",\"payload\":{\"steamId\":\"76561197979952036\",\"playerName\":\"Doctor Delete\",\"reason\":\"Disconnected\"}}"));

        assertEquals(1, subscriber.events.size());
        assertEquals("76561197979952036", subscriber.events.get(0).getSteamId().get());
        assertEquals("Disconnected", subscriber.events.get(0).getReason());
    }

    @Test
    void emitsPlayerDeathEventFromBridgeEnvelope() {
        final var eventBus = new EventBus();
        final var eventService = new UmodBridgeRustEventService(eventBus);
        final var subscriber = new DeathSubscriber();
        eventBus.register(subscriber);
        eventService.configure();

        eventBus.post(rconReceived("[rust-rcon] {\"schemaVersion\":1,\"eventType\":\"player.death\",\"eventId\":\"evt-4\",\"timestamp\":\"2026-05-27T12:00:00Z\",\"payload\":{\"victim\":\"Bear\",\"killer\":\"Doctor Delete\",\"bodypart\":\"Body\",\"distance\":\"27.4 meters\",\"hp\":\"100\",\"weapon\":\"Assault Rifle\",\"attachments\":\"Weapon flashlight, Extended Magazine, Muzzle Brake\",\"killerId\":\"76561197979952036\",\"victimId\":null,\"damageType\":\"Bullet\",\"killerEntityType\":\"Player\",\"victimEntityType\":\"Animal\"}}"));

        assertEquals(1, subscriber.events.size());
        assertEquals("Bear", subscriber.events.get(0).getVictim());
        assertEquals("Doctor Delete", subscriber.events.get(0).getKiller());
        assertEquals(new BigDecimal("27.4"), subscriber.events.get(0).getDistance());
        assertEquals(new BigDecimal("100"), subscriber.events.get(0).getKillerHealth());
        assertEquals("Assault Rifle", subscriber.events.get(0).getWeapon());
        assertEquals(Set.of("Weapon flashlight", "Extended Magazine", "Muzzle Brake"), subscriber.events.get(0).getAttachments());
        assertEquals("76561197979952036", subscriber.events.get(0).getSteamId().get());
        assertEquals(CombatTypes.PVE, subscriber.events.get(0).getCombatType());
        assertEquals(DamageTypes.BULLET, subscriber.events.get(0).getDamageType());
    }

    @Test
    void emitsDiagnosticForMalformedBridgeJson() {
        final var eventBus = new EventBus();
        final var eventService = new UmodBridgeRustEventService(eventBus);
        final var subscriber = new DiagnosticSubscriber();
        eventBus.register(subscriber);
        eventService.configure();

        eventBus.post(rconReceived("[rust-rcon] {nope"));

        assertEquals(1, subscriber.events.size());
        assertEquals(UmodBridgeDiagnosticType.MALFORMED_JSON, subscriber.events.get(0).getDiagnosticType());
    }

    @Test
    void emitsDiagnosticForUnsupportedSchemaVersion() {
        final var eventBus = new EventBus();
        final var eventService = new UmodBridgeRustEventService(eventBus);
        final var subscriber = new DiagnosticSubscriber();
        eventBus.register(subscriber);
        eventService.configure();

        eventBus.post(rconReceived("[rust-rcon] {\"schemaVersion\":2,\"eventType\":\"player.chat\",\"payload\":{}}"));

        assertEquals(1, subscriber.events.size());
        assertEquals(UmodBridgeDiagnosticType.UNSUPPORTED_SCHEMA_VERSION, subscriber.events.get(0).getDiagnosticType());
    }

    private RconReceivedEvent rconReceived(String message) {
        return new RconReceivedEvent("test", new TestRustRconResponse(message));
    }

    static class ChatSubscriber {
        private final List<PlayerChatEvent> events = new ArrayList<>();

        @Subscribe
        public void onPlayerChat(PlayerChatEvent event) {
            events.add(event);
        }
    }

    static class DiagnosticSubscriber {
        private final List<UmodBridgeDiagnosticEvent> events = new ArrayList<>();

        @Subscribe
        public void onDiagnostic(UmodBridgeDiagnosticEvent event) {
            events.add(event);
        }
    }

    static class ConnectedSubscriber {
        private final List<PlayerConnectedEvent> events = new ArrayList<>();

        @Subscribe
        public void onPlayerConnected(PlayerConnectedEvent event) {
            events.add(event);
        }
    }

    static class DisconnectedSubscriber {
        private final List<PlayerDisconnectedEvent> events = new ArrayList<>();

        @Subscribe
        public void onPlayerDisconnected(PlayerDisconnectedEvent event) {
            events.add(event);
        }
    }

    static class DeathSubscriber {
        private final List<PlayerDeathEvent> events = new ArrayList<>();

        @Subscribe
        public void onPlayerDeath(PlayerDeathEvent event) {
            events.add(event);
        }
    }
}
