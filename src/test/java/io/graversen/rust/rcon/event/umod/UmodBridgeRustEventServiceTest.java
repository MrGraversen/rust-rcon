package io.graversen.rust.rcon.event.umod;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import io.graversen.rust.rcon.TestRustRconResponse;
import io.graversen.rust.rcon.event.RustEventSourceStrategy;
import io.graversen.rust.rcon.event.player.PlayerBannedEvent;
import io.graversen.rust.rcon.event.player.PlayerChatEvent;
import io.graversen.rust.rcon.event.player.PlayerConnectedEvent;
import io.graversen.rust.rcon.event.player.PlayerDeathEvent;
import io.graversen.rust.rcon.event.player.PlayerDisconnectedEvent;
import io.graversen.rust.rcon.event.player.PlayerKickedEvent;
import io.graversen.rust.rcon.event.player.PlayerRecoveredEvent;
import io.graversen.rust.rcon.event.player.PlayerReportedEvent;
import io.graversen.rust.rcon.event.player.PlayerRespawnedEvent;
import io.graversen.rust.rcon.event.player.PlayerUnbannedEvent;
import io.graversen.rust.rcon.event.player.PlayerViolationEvent;
import io.graversen.rust.rcon.event.player.PlayerWoundedEvent;
import io.graversen.rust.rcon.event.rcon.RconReceivedEvent;
import io.graversen.rust.rcon.event.server.ExplosiveUseEvent;
import io.graversen.rust.rcon.event.server.ExplosiveUseTypes;
import io.graversen.rust.rcon.event.server.SaveEvent;
import io.graversen.rust.rcon.event.server.ServerInitializedEvent;
import io.graversen.rust.rcon.event.server.ServerShutdownEvent;
import io.graversen.rust.rcon.event.server.TeamEvent;
import io.graversen.rust.rcon.event.server.TeamEventTypes;
import io.graversen.rust.rcon.event.server.WorldEvent;
import io.graversen.rust.rcon.protocol.util.ChatChannels;
import io.graversen.rust.rcon.protocol.util.CombatTypes;
import io.graversen.rust.rcon.protocol.util.DamageTypes;
import io.graversen.rust.rcon.protocol.util.OperatingSystems;
import io.graversen.rust.rcon.protocol.util.WorldEvents;
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
        assertTrue(capabilities.supports(ExplosiveUseEvent.class));
        assertTrue(capabilities.supports(PlayerBannedEvent.class));
        assertTrue(capabilities.supports(PlayerChatEvent.class));
        assertTrue(capabilities.supports(PlayerConnectedEvent.class));
        assertTrue(capabilities.supports(PlayerDeathEvent.class));
        assertTrue(capabilities.supports(PlayerDisconnectedEvent.class));
        assertTrue(capabilities.supports(PlayerKickedEvent.class));
        assertTrue(capabilities.supports(PlayerRecoveredEvent.class));
        assertTrue(capabilities.supports(PlayerReportedEvent.class));
        assertTrue(capabilities.supports(PlayerRespawnedEvent.class));
        assertTrue(capabilities.supports(PlayerUnbannedEvent.class));
        assertTrue(capabilities.supports(PlayerViolationEvent.class));
        assertTrue(capabilities.supports(PlayerWoundedEvent.class));
        assertTrue(capabilities.supports(SaveEvent.class));
        assertTrue(capabilities.supports(ServerInitializedEvent.class));
        assertTrue(capabilities.supports(ServerShutdownEvent.class));
        assertTrue(capabilities.supports(TeamEvent.class));
        assertTrue(capabilities.supports(WorldEvent.class));
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
    void emitsPlayerDisconnectedEventFromPluginPrefixedBridgeEnvelope() {
        final var eventBus = new EventBus();
        final var eventService = new UmodBridgeRustEventService(eventBus);
        final var subscriber = new DisconnectedSubscriber();
        eventBus.register(subscriber);
        eventService.configure();

        eventBus.post(rconReceived("[RustRconBridge] [rust-rcon] {\"schemaVersion\":1,\"eventType\":\"player.disconnected\",\"eventId\":\"c1ccec3986f643388dac0020acbf7cca\",\"timestamp\":\"2026-06-06T10:27:50.2828820Z\",\"payload\":{\"steamId\":\"76561197979952036\",\"playerName\":\"Doctor Delete\",\"reason\":\"Disconnected\"}}"));

        assertEquals(1, subscriber.events.size());
        assertEquals("76561197979952036", subscriber.events.get(0).getSteamId().get());
        assertEquals("Disconnected", subscriber.events.get(0).getReason());
    }

    @Test
    void emitsPlayerDisconnectedEventFromPluginPrefixedJsonEnvelope() {
        final var eventBus = new EventBus();
        final var eventService = new UmodBridgeRustEventService(eventBus);
        final var subscriber = new DisconnectedSubscriber();
        eventBus.register(subscriber);
        eventService.configure();

        eventBus.post(rconReceived("[RustRconBridge] {\"schemaVersion\":1,\"eventType\":\"player.disconnected\",\"eventId\":\"c1ccec3986f643388dac0020acbf7cca\",\"timestamp\":\"2026-06-06T10:27:50.2828820Z\",\"payload\":{\"steamId\":\"76561197979952036\",\"playerName\":\"Doctor Delete\",\"reason\":\"Disconnected\"}}"));

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
    void emitsPlayerRespawnedEventFromBridgeEnvelope() {
        final var eventBus = new EventBus();
        final var eventService = new UmodBridgeRustEventService(eventBus);
        final var subscriber = new RespawnedSubscriber();
        eventBus.register(subscriber);
        eventService.configure();

        eventBus.post(rconReceived("[rust-rcon] {\"schemaVersion\":1,\"eventType\":\"player.respawned\",\"eventId\":\"evt-5\",\"timestamp\":\"2026-05-27T12:00:00Z\",\"payload\":{\"steamId\":\"76561197979952036\",\"playerName\":\"Doctor Delete\"}}"));

        assertEquals(1, subscriber.events.size());
        assertEquals("76561197979952036", subscriber.events.get(0).getSteamId().get());
        assertEquals("Doctor Delete", subscriber.events.get(0).getPlayerName().get());
    }

    @Test
    void emitsPlayerWoundedEventFromBridgeEnvelope() {
        final var eventBus = new EventBus();
        final var eventService = new UmodBridgeRustEventService(eventBus);
        final var subscriber = new WoundedSubscriber();
        eventBus.register(subscriber);
        eventService.configure();

        eventBus.post(rconReceived("[rust-rcon] {\"schemaVersion\":1,\"eventType\":\"player.wounded\",\"eventId\":\"evt-6\",\"timestamp\":\"2026-05-27T12:00:00Z\",\"payload\":{\"steamId\":\"76561197979952036\",\"playerName\":\"Doctor Delete\"}}"));

        assertEquals(1, subscriber.events.size());
        assertEquals("76561197979952036", subscriber.events.get(0).getSteamId().get());
        assertEquals("Doctor Delete", subscriber.events.get(0).getPlayerName().get());
    }

    @Test
    void emitsPlayerRecoveredEventFromBridgeEnvelope() {
        final var eventBus = new EventBus();
        final var eventService = new UmodBridgeRustEventService(eventBus);
        final var subscriber = new RecoveredSubscriber();
        eventBus.register(subscriber);
        eventService.configure();

        eventBus.post(rconReceived("[rust-rcon] {\"schemaVersion\":1,\"eventType\":\"player.recovered\",\"eventId\":\"evt-7\",\"timestamp\":\"2026-05-27T12:00:00Z\",\"payload\":{\"steamId\":\"76561197979952036\",\"playerName\":\"Doctor Delete\"}}"));

        assertEquals(1, subscriber.events.size());
        assertEquals("76561197979952036", subscriber.events.get(0).getSteamId().get());
        assertEquals("Doctor Delete", subscriber.events.get(0).getPlayerName().get());
    }

    @Test
    void emitsServerInitializedEventFromBridgeEnvelope() {
        final var eventBus = new EventBus();
        final var eventService = new UmodBridgeRustEventService(eventBus);
        final var subscriber = new ServerInitializedSubscriber();
        eventBus.register(subscriber);
        eventService.configure();

        eventBus.post(rconReceived("[rust-rcon] {\"schemaVersion\":1,\"eventType\":\"server.initialized\",\"eventId\":\"evt-8\",\"timestamp\":\"2026-05-27T12:00:00Z\",\"payload\":{}}"));

        assertEquals(1, subscriber.events.size());
        assertEquals("test", subscriber.events.get(0).getServer().getName());
    }

    @Test
    void emitsSaveEventFromBridgeEnvelope() {
        final var eventBus = new EventBus();
        final var eventService = new UmodBridgeRustEventService(eventBus);
        final var subscriber = new SaveSubscriber();
        eventBus.register(subscriber);
        eventService.configure();

        eventBus.post(rconReceived("[rust-rcon] {\"schemaVersion\":1,\"eventType\":\"server.save\",\"eventId\":\"evt-9\",\"timestamp\":\"2026-05-27T12:00:00Z\",\"payload\":{}}"));

        assertEquals(1, subscriber.events.size());
        assertEquals("test", subscriber.events.get(0).getServer().getName());
    }

    @Test
    void emitsServerShutdownEventFromBridgeEnvelope() {
        final var eventBus = new EventBus();
        final var eventService = new UmodBridgeRustEventService(eventBus);
        final var subscriber = new ServerShutdownSubscriber();
        eventBus.register(subscriber);
        eventService.configure();

        eventBus.post(rconReceived("[rust-rcon] {\"schemaVersion\":1,\"eventType\":\"server.shutdown\",\"eventId\":\"evt-10\",\"timestamp\":\"2026-05-27T12:00:00Z\",\"payload\":{}}"));

        assertEquals(1, subscriber.events.size());
        assertEquals("test", subscriber.events.get(0).getServer().getName());
    }

    @Test
    void emitsPlayerKickedEventFromBridgeEnvelope() {
        final var eventBus = new EventBus();
        final var eventService = new UmodBridgeRustEventService(eventBus);
        final var subscriber = new KickedSubscriber();
        eventBus.register(subscriber);
        eventService.configure();

        eventBus.post(rconReceived("[rust-rcon] {\"schemaVersion\":1,\"eventType\":\"player.kicked\",\"eventId\":\"evt-11\",\"timestamp\":\"2026-05-27T12:00:00Z\",\"payload\":{\"steamId\":\"76561197979952036\",\"playerName\":\"Doctor Delete\",\"ipAddress\":\"127.0.0.1\",\"reason\":\"Too spicy\"}}"));

        assertEquals(1, subscriber.events.size());
        assertEquals("76561197979952036", subscriber.events.get(0).getSteamId().get());
        assertEquals("Doctor Delete", subscriber.events.get(0).getPlayerName().get());
        assertEquals("127.0.0.1", subscriber.events.get(0).getIpAddress());
        assertEquals("Too spicy", subscriber.events.get(0).getReason());
    }

    @Test
    void emitsPlayerBannedEventFromBridgeEnvelope() {
        final var eventBus = new EventBus();
        final var eventService = new UmodBridgeRustEventService(eventBus);
        final var subscriber = new BannedSubscriber();
        eventBus.register(subscriber);
        eventService.configure();

        eventBus.post(rconReceived("[rust-rcon] {\"schemaVersion\":1,\"eventType\":\"player.banned\",\"eventId\":\"evt-12\",\"timestamp\":\"2026-05-27T12:00:00Z\",\"payload\":{\"steamId\":\"76561197979952036\",\"playerName\":\"Doctor Delete\",\"ipAddress\":\"127.0.0.1\",\"reason\":\"Nope\",\"expiry\":1790000000}}"));

        assertEquals(1, subscriber.events.size());
        assertEquals("76561197979952036", subscriber.events.get(0).getSteamId().get());
        assertEquals("Doctor Delete", subscriber.events.get(0).getPlayerName().get());
        assertEquals("127.0.0.1", subscriber.events.get(0).getIpAddress());
        assertEquals("Nope", subscriber.events.get(0).getReason());
        assertEquals(1790000000L, subscriber.events.get(0).getExpiry());
    }

    @Test
    void emitsPlayerUnbannedEventFromBridgeEnvelope() {
        final var eventBus = new EventBus();
        final var eventService = new UmodBridgeRustEventService(eventBus);
        final var subscriber = new UnbannedSubscriber();
        eventBus.register(subscriber);
        eventService.configure();

        eventBus.post(rconReceived("[rust-rcon] {\"schemaVersion\":1,\"eventType\":\"player.unbanned\",\"eventId\":\"evt-13\",\"timestamp\":\"2026-05-27T12:00:00Z\",\"payload\":{\"steamId\":\"76561197979952036\",\"playerName\":\"Doctor Delete\",\"ipAddress\":\"127.0.0.1\"}}"));

        assertEquals(1, subscriber.events.size());
        assertEquals("76561197979952036", subscriber.events.get(0).getSteamId().get());
        assertEquals("Doctor Delete", subscriber.events.get(0).getPlayerName().get());
        assertEquals("127.0.0.1", subscriber.events.get(0).getIpAddress());
    }

    @Test
    void emitsPlayerReportedEventFromBridgeEnvelope() {
        final var eventBus = new EventBus();
        final var eventService = new UmodBridgeRustEventService(eventBus);
        final var subscriber = new ReportedSubscriber();
        eventBus.register(subscriber);
        eventService.configure();

        eventBus.post(rconReceived("[rust-rcon] {\"schemaVersion\":1,\"eventType\":\"player.reported\",\"eventId\":\"evt-14\",\"timestamp\":\"2026-05-27T12:00:00Z\",\"payload\":{\"reporterSteamId\":\"76561197979952036\",\"reporterName\":\"Doctor Delete\",\"targetSteamId\":\"76561197979952037\",\"targetName\":\"Professor Create\",\"subject\":\"Cheating\",\"message\":\"Suspicious recoil\",\"reportType\":\"abuse\"}}"));

        assertEquals(1, subscriber.events.size());
        assertEquals("76561197979952036", subscriber.events.get(0).getReporterSteamId().get());
        assertEquals("Doctor Delete", subscriber.events.get(0).getReporterName().get());
        assertEquals("76561197979952037", subscriber.events.get(0).getTargetSteamId().get());
        assertEquals("Professor Create", subscriber.events.get(0).getTargetName().get());
        assertEquals("Cheating", subscriber.events.get(0).getSubject());
        assertEquals("Suspicious recoil", subscriber.events.get(0).getMessage());
        assertEquals("abuse", subscriber.events.get(0).getReportType());
    }

    @Test
    void emitsPlayerViolationEventFromBridgeEnvelope() {
        final var eventBus = new EventBus();
        final var eventService = new UmodBridgeRustEventService(eventBus);
        final var subscriber = new ViolationSubscriber();
        eventBus.register(subscriber);
        eventService.configure();

        eventBus.post(rconReceived("[rust-rcon] {\"schemaVersion\":1,\"eventType\":\"player.violation\",\"eventId\":\"evt-15\",\"timestamp\":\"2026-05-27T12:00:00Z\",\"payload\":{\"steamId\":\"76561197979952036\",\"playerName\":\"Doctor Delete\",\"violationType\":\"FlyHack\",\"amount\":132.5}}"));

        assertEquals(1, subscriber.events.size());
        assertEquals("76561197979952036", subscriber.events.get(0).getSteamId().get());
        assertEquals("Doctor Delete", subscriber.events.get(0).getPlayerName().get());
        assertEquals("FlyHack", subscriber.events.get(0).getViolationType());
        assertEquals(new BigDecimal("132.5"), subscriber.events.get(0).getAmount());
    }

    @Test
    void emitsTeamEventFromBridgeEnvelope() {
        final var eventBus = new EventBus();
        final var eventService = new UmodBridgeRustEventService(eventBus);
        final var subscriber = new TeamSubscriber();
        eventBus.register(subscriber);
        eventService.configure();

        eventBus.post(rconReceived("[rust-rcon] {\"schemaVersion\":1,\"eventType\":\"team\",\"eventId\":\"evt-16\",\"timestamp\":\"2026-05-27T12:00:00Z\",\"payload\":{\"teamId\":42,\"leaderSteamId\":76561197979952036,\"teamEventType\":\"created\",\"actorSteamId\":\"76561197979952036\",\"actorName\":\"Doctor Delete\",\"targetSteamId\":\"\",\"members\":[\"76561197979952036\",\"76561197979952037\"]}}"));

        assertEquals(1, subscriber.events.size());
        assertEquals(42L, subscriber.events.get(0).getTeamId());
        assertEquals(76561197979952036L, subscriber.events.get(0).getLeaderSteamId());
        assertEquals(TeamEventTypes.CREATED, subscriber.events.get(0).getTeamEventType());
        assertEquals("76561197979952036", subscriber.events.get(0).getActorSteamId().get());
        assertEquals("Doctor Delete", subscriber.events.get(0).getActorName().get());
        assertEquals(Set.of("76561197979952036", "76561197979952037"), subscriber.events.get(0).getMembers());
    }

    @Test
    void emitsExplosiveUseEventFromBridgeEnvelope() {
        final var eventBus = new EventBus();
        final var eventService = new UmodBridgeRustEventService(eventBus);
        final var subscriber = new ExplosiveUseSubscriber();
        eventBus.register(subscriber);
        eventService.configure();

        eventBus.post(rconReceived("[rust-rcon] {\"schemaVersion\":1,\"eventType\":\"explosive.use\",\"eventId\":\"evt-17\",\"timestamp\":\"2026-05-27T12:00:00Z\",\"payload\":{\"steamId\":\"76561197979952036\",\"playerName\":\"Doctor Delete\",\"explosiveUseType\":\"rocket\",\"weapon\":\"rocket.launcher\",\"entity\":\"rocket_basic\",\"position\":\"1.25,2,3.5\"}}"));

        assertEquals(1, subscriber.events.size());
        assertEquals("76561197979952036", subscriber.events.get(0).getSteamId().get());
        assertEquals("Doctor Delete", subscriber.events.get(0).getPlayerName().get());
        assertEquals(ExplosiveUseTypes.ROCKET, subscriber.events.get(0).getExplosiveUseType());
        assertEquals("rocket.launcher", subscriber.events.get(0).getWeapon());
        assertEquals("rocket_basic", subscriber.events.get(0).getEntity());
        assertEquals("1.25,2,3.5", subscriber.events.get(0).getPosition());
    }

    @Test
    void emitsWorldEventFromBridgeEnvelope() {
        final var eventBus = new EventBus();
        final var eventService = new UmodBridgeRustEventService(eventBus);
        final var subscriber = new WorldSubscriber();
        eventBus.register(subscriber);
        eventService.configure();

        eventBus.post(rconReceived("[rust-rcon] {\"schemaVersion\":1,\"eventType\":\"world.event\",\"eventId\":\"evt-18\",\"timestamp\":\"2026-05-27T12:00:00Z\",\"payload\":{\"worldEvent\":\"locked_crate_hack_started\",\"attributes\":{\"steamId\":\"76561197979952036\",\"playerName\":\"Doctor Delete\",\"entityId\":\"1234\",\"entity\":\"codelockedhackablecrate\",\"position\":\"1.25,2,3.5\"}}}"));

        assertEquals(1, subscriber.events.size());
        assertEquals(WorldEvents.LOCKED_CRATE_HACK_STARTED, subscriber.events.get(0).getEvent());
        assertEquals("test", subscriber.events.get(0).getServer().getName());
        assertEquals("76561197979952036", subscriber.events.get(0).getAttributes().get("steamId"));
        assertEquals("Doctor Delete", subscriber.events.get(0).getAttributes().get("playerName"));
        assertEquals("1234", subscriber.events.get(0).getAttributes().get("entityId"));
        assertEquals("codelockedhackablecrate", subscriber.events.get(0).getAttributes().get("entity"));
        assertEquals("1.25,2,3.5", subscriber.events.get(0).getAttributes().get("position"));
    }

    @Test
    void emitsCargoShipWorldEventFromBridgeEnvelope() {
        final var eventBus = new EventBus();
        final var eventService = new UmodBridgeRustEventService(eventBus);
        final var subscriber = new WorldSubscriber();
        eventBus.register(subscriber);
        eventService.configure();

        eventBus.post(rconReceived("[rust-rcon] {\"schemaVersion\":1,\"eventType\":\"world.event\",\"eventId\":\"evt-19\",\"timestamp\":\"2026-05-27T12:00:00Z\",\"payload\":{\"worldEvent\":\"cargo_ship_harbor_arrived\",\"attributes\":{\"entityId\":\"4321\",\"entity\":\"cargoshiptest\",\"position\":\"10,20,30\"}}}"));

        assertEquals(1, subscriber.events.size());
        assertEquals(WorldEvents.CARGO_SHIP_HARBOR_ARRIVED, subscriber.events.get(0).getEvent());
        assertEquals("4321", subscriber.events.get(0).getAttributes().get("entityId"));
        assertEquals("cargoshiptest", subscriber.events.get(0).getAttributes().get("entity"));
    }

    @Test
    void emitsSupplyDropWorldEventFromBridgeEnvelope() {
        final var eventBus = new EventBus();
        final var eventService = new UmodBridgeRustEventService(eventBus);
        final var subscriber = new WorldSubscriber();
        eventBus.register(subscriber);
        eventService.configure();

        eventBus.post(rconReceived("[rust-rcon] {\"schemaVersion\":1,\"eventType\":\"world.event\",\"eventId\":\"evt-20\",\"timestamp\":\"2026-05-27T12:00:00Z\",\"payload\":{\"worldEvent\":\"supply_drop_dropped\",\"attributes\":{\"entityId\":\"9876\",\"entity\":\"supply_drop\",\"position\":\"10,20,30\",\"planeEntityId\":\"1234\",\"planePosition\":\"1,2,3\"}}}"));

        assertEquals(1, subscriber.events.size());
        assertEquals(WorldEvents.SUPPLY_DROP_DROPPED, subscriber.events.get(0).getEvent());
        assertEquals("9876", subscriber.events.get(0).getAttributes().get("entityId"));
        assertEquals("1234", subscriber.events.get(0).getAttributes().get("planeEntityId"));
        assertEquals("1,2,3", subscriber.events.get(0).getAttributes().get("planePosition"));
    }

    @Test
    void emitsBossWorldEventFromBridgeEnvelope() {
        final var eventBus = new EventBus();
        final var eventService = new UmodBridgeRustEventService(eventBus);
        final var subscriber = new WorldSubscriber();
        eventBus.register(subscriber);
        eventService.configure();

        eventBus.post(rconReceived("[rust-rcon] {\"schemaVersion\":1,\"eventType\":\"world.event\",\"eventId\":\"evt-21\",\"timestamp\":\"2026-05-27T12:00:00Z\",\"payload\":{\"worldEvent\":\"patrol_helicopter_killed\",\"attributes\":{\"entityId\":\"3333\",\"entity\":\"patrolhelicopter\",\"position\":\"10,20,30\",\"killerSteamId\":\"76561197979952036\",\"killerName\":\"Doctor Delete\"}}}"));

        assertEquals(1, subscriber.events.size());
        assertEquals(WorldEvents.PATROL_HELICOPTER_KILLED, subscriber.events.get(0).getEvent());
        assertEquals("3333", subscriber.events.get(0).getAttributes().get("entityId"));
        assertEquals("patrolhelicopter", subscriber.events.get(0).getAttributes().get("entity"));
        assertEquals("76561197979952036", subscriber.events.get(0).getAttributes().get("killerSteamId"));
        assertEquals("Doctor Delete", subscriber.events.get(0).getAttributes().get("killerName"));
    }

    @Test
    void emitsMlrsWorldEventFromBridgeEnvelope() {
        final var eventBus = new EventBus();
        final var eventService = new UmodBridgeRustEventService(eventBus);
        final var subscriber = new WorldSubscriber();
        eventBus.register(subscriber);
        eventService.configure();

        eventBus.post(rconReceived("[rust-rcon] {\"schemaVersion\":1,\"eventType\":\"world.event\",\"eventId\":\"evt-22\",\"timestamp\":\"2026-05-27T12:00:00Z\",\"payload\":{\"worldEvent\":\"mlrs_fired\",\"attributes\":{\"entityId\":\"4444\",\"entity\":\"mlrs\",\"position\":\"10,20,30\",\"ownerSteamId\":\"76561197979952036\",\"ownerName\":\"Doctor Delete\"}}}"));

        assertEquals(1, subscriber.events.size());
        assertEquals(WorldEvents.MLRS_FIRED, subscriber.events.get(0).getEvent());
        assertEquals("4444", subscriber.events.get(0).getAttributes().get("entityId"));
        assertEquals("76561197979952036", subscriber.events.get(0).getAttributes().get("ownerSteamId"));
        assertEquals("Doctor Delete", subscriber.events.get(0).getAttributes().get("ownerName"));
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

    static class RespawnedSubscriber {
        private final List<PlayerRespawnedEvent> events = new ArrayList<>();

        @Subscribe
        public void onPlayerRespawned(PlayerRespawnedEvent event) {
            events.add(event);
        }
    }

    static class WoundedSubscriber {
        private final List<PlayerWoundedEvent> events = new ArrayList<>();

        @Subscribe
        public void onPlayerWounded(PlayerWoundedEvent event) {
            events.add(event);
        }
    }

    static class RecoveredSubscriber {
        private final List<PlayerRecoveredEvent> events = new ArrayList<>();

        @Subscribe
        public void onPlayerRecovered(PlayerRecoveredEvent event) {
            events.add(event);
        }
    }

    static class ServerInitializedSubscriber {
        private final List<ServerInitializedEvent> events = new ArrayList<>();

        @Subscribe
        public void onServerInitialized(ServerInitializedEvent event) {
            events.add(event);
        }
    }

    static class SaveSubscriber {
        private final List<SaveEvent> events = new ArrayList<>();

        @Subscribe
        public void onSave(SaveEvent event) {
            events.add(event);
        }
    }

    static class ServerShutdownSubscriber {
        private final List<ServerShutdownEvent> events = new ArrayList<>();

        @Subscribe
        public void onServerShutdown(ServerShutdownEvent event) {
            events.add(event);
        }
    }

    static class KickedSubscriber {
        private final List<PlayerKickedEvent> events = new ArrayList<>();

        @Subscribe
        public void onPlayerKicked(PlayerKickedEvent event) {
            events.add(event);
        }
    }

    static class BannedSubscriber {
        private final List<PlayerBannedEvent> events = new ArrayList<>();

        @Subscribe
        public void onPlayerBanned(PlayerBannedEvent event) {
            events.add(event);
        }
    }

    static class UnbannedSubscriber {
        private final List<PlayerUnbannedEvent> events = new ArrayList<>();

        @Subscribe
        public void onPlayerUnbanned(PlayerUnbannedEvent event) {
            events.add(event);
        }
    }

    static class ReportedSubscriber {
        private final List<PlayerReportedEvent> events = new ArrayList<>();

        @Subscribe
        public void onPlayerReported(PlayerReportedEvent event) {
            events.add(event);
        }
    }

    static class ViolationSubscriber {
        private final List<PlayerViolationEvent> events = new ArrayList<>();

        @Subscribe
        public void onPlayerViolation(PlayerViolationEvent event) {
            events.add(event);
        }
    }

    static class TeamSubscriber {
        private final List<TeamEvent> events = new ArrayList<>();

        @Subscribe
        public void onTeam(TeamEvent event) {
            events.add(event);
        }
    }

    static class ExplosiveUseSubscriber {
        private final List<ExplosiveUseEvent> events = new ArrayList<>();

        @Subscribe
        public void onExplosiveUse(ExplosiveUseEvent event) {
            events.add(event);
        }
    }

    static class WorldSubscriber {
        private final List<WorldEvent> events = new ArrayList<>();

        @Subscribe
        public void onWorld(WorldEvent event) {
            events.add(event);
        }
    }
}
