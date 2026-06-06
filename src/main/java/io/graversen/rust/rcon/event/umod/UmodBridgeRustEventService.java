package io.graversen.rust.rcon.event.umod;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.fasterxml.jackson.databind.JsonNode;
import io.graversen.rust.rcon.event.BaseEventHandler;
import io.graversen.rust.rcon.event.RustEvent;
import io.graversen.rust.rcon.event.RustEventCapabilities;
import io.graversen.rust.rcon.event.RustEventService;
import io.graversen.rust.rcon.event.RustEventSourceStrategy;
import io.graversen.rust.rcon.event.player.PlayerBannedEvent;
import io.graversen.rust.rcon.event.player.PlayerChatEvent;
import io.graversen.rust.rcon.event.player.PlayerConnectedEvent;
import io.graversen.rust.rcon.event.player.PlayerDeathDTO;
import io.graversen.rust.rcon.event.player.PlayerDeathEvent;
import io.graversen.rust.rcon.event.player.PlayerDeathEventParser;
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
import io.graversen.rust.rcon.protocol.util.OperatingSystems;
import io.graversen.rust.rcon.protocol.util.PlayerName;
import io.graversen.rust.rcon.protocol.util.SteamId64;
import io.graversen.rust.rcon.protocol.util.WorldEvents;
import io.graversen.rust.rcon.util.DefaultJsonMapper;
import io.graversen.rust.rcon.util.JsonMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
public class UmodBridgeRustEventService extends BaseEventHandler implements RustEventService {
    public static final String BRIDGE_PREFIX = "[" + RustRconBridgePlugin.PLUGIN_NAME + "]";
    private static final String LEGACY_BRIDGE_PREFIX = "[rust-rcon]";

    private static final String EXPLOSIVE_USE_EVENT_TYPE = "explosive.use";
    private static final String PLAYER_BANNED_EVENT_TYPE = "player.banned";
    private static final String PLAYER_CHAT_EVENT_TYPE = "player.chat";
    private static final String PLAYER_CONNECTED_EVENT_TYPE = "player.connected";
    private static final String PLAYER_DEATH_EVENT_TYPE = "player.death";
    private static final String PLAYER_DISCONNECTED_EVENT_TYPE = "player.disconnected";
    private static final String PLAYER_KICKED_EVENT_TYPE = "player.kicked";
    private static final String PLAYER_RECOVERED_EVENT_TYPE = "player.recovered";
    private static final String PLAYER_REPORTED_EVENT_TYPE = "player.reported";
    private static final String PLAYER_RESPAWNED_EVENT_TYPE = "player.respawned";
    private static final String PLAYER_UNBANNED_EVENT_TYPE = "player.unbanned";
    private static final String PLAYER_VIOLATION_EVENT_TYPE = "player.violation";
    private static final String PLAYER_WOUNDED_EVENT_TYPE = "player.wounded";
    private static final String SERVER_INITIALIZED_EVENT_TYPE = "server.initialized";
    private static final String SERVER_SAVE_EVENT_TYPE = "server.save";
    private static final String SERVER_SHUTDOWN_EVENT_TYPE = "server.shutdown";
    private static final String TEAM_EVENT_TYPE = "team";
    private static final String WORLD_EVENT_TYPE = "world.event";

    private final @NonNull EventBus eventBus;
    private final @NonNull JsonMapper jsonMapper = new DefaultJsonMapper();
    private final @NonNull PlayerDeathEventParser playerDeathEventParser = new PlayerDeathEventParser();

    @Subscribe
    @Override
    public void onRconReceived(@NonNull RconReceivedEvent event) {
        final var message = event.getRconResponse().getMessage();
        if (bridgeJson(message).isEmpty()) {
            return;
        }

        parseEnvelope(message)
                .flatMap(envelope -> {
                    logBridgeEnvelope(event, envelope);
                    return parseEvent(event, envelope);
                })
                .ifPresent(eventBus::post);
    }

    @Override
    public void configure() {
        eventBus.register(this);
    }

    @Override
    public RustEventCapabilities capabilities() {
        return new RustEventCapabilities(
                RustEventSourceStrategy.UMOD,
                Set.of(
                        ExplosiveUseEvent.class,
                        PlayerBannedEvent.class,
                        PlayerChatEvent.class,
                        PlayerConnectedEvent.class,
                        PlayerDeathEvent.class,
                        PlayerDisconnectedEvent.class,
                        PlayerKickedEvent.class,
                        PlayerRecoveredEvent.class,
                        PlayerReportedEvent.class,
                        PlayerRespawnedEvent.class,
                        PlayerUnbannedEvent.class,
                        PlayerViolationEvent.class,
                        PlayerWoundedEvent.class,
                        SaveEvent.class,
                        ServerInitializedEvent.class,
                        ServerShutdownEvent.class,
                        TeamEvent.class,
                        WorldEvent.class,
                        UmodBridgeDiagnosticEvent.class
                )
        );
    }

    private Optional<UmodBridgeEnvelope> parseEnvelope(@NonNull String rawMessage) {
        final var json = bridgeJson(rawMessage);
        if (json.isEmpty()) {
            return Optional.empty();
        }
        try {
            final var envelope = jsonMapper.fromJson(json.get(), UmodBridgeEnvelope.class);
            if (envelope.getSchemaVersion() == null || envelope.getSchemaVersion() != RustRconBridgePlugin.SCHEMA_VERSION) {
                emitDiagnostic(
                        UmodBridgeDiagnosticType.UNSUPPORTED_SCHEMA_VERSION,
                        rawMessage,
                        String.format("Expected schema version %d but got %s", RustRconBridgePlugin.SCHEMA_VERSION, envelope.getSchemaVersion())
                );
                return Optional.empty();
            }

            return Optional.of(envelope);
        } catch (Exception e) {
            emitDiagnostic(UmodBridgeDiagnosticType.MALFORMED_JSON, rawMessage, e.getMessage());
            return Optional.empty();
        }
    }

    private Optional<String> bridgeJson(@NonNull String rawMessage) {
        final var legacyBridgePrefixIndex = rawMessage.indexOf(LEGACY_BRIDGE_PREFIX);
        if (legacyBridgePrefixIndex >= 0) {
            return Optional.of(rawMessage.substring(legacyBridgePrefixIndex + LEGACY_BRIDGE_PREFIX.length()).trim());
        }

        final var bridgePrefixIndex = rawMessage.indexOf(BRIDGE_PREFIX);
        if (bridgePrefixIndex >= 0) {
            return Optional.of(rawMessage.substring(bridgePrefixIndex + BRIDGE_PREFIX.length()).trim());
        }

        return Optional.empty();
    }

    private Optional<RustEvent> parseEvent(@NonNull RconReceivedEvent event, @NonNull UmodBridgeEnvelope envelope) {
        final var rawMessage = event.getRconResponse().getMessage();
        if (EXPLOSIVE_USE_EVENT_TYPE.equalsIgnoreCase(envelope.getEventType())) {
            return parseExplosiveUse(rawMessage, envelope.getPayload()).map(RustEvent.class::cast);
        } else if (PLAYER_BANNED_EVENT_TYPE.equalsIgnoreCase(envelope.getEventType())) {
            return parsePlayerBan(rawMessage, envelope.getPayload()).map(RustEvent.class::cast);
        } else if (PLAYER_CHAT_EVENT_TYPE.equalsIgnoreCase(envelope.getEventType())) {
            return parsePlayerChat(rawMessage, envelope.getPayload()).map(RustEvent.class::cast);
        } else if (PLAYER_CONNECTED_EVENT_TYPE.equalsIgnoreCase(envelope.getEventType())) {
            return parsePlayerConnected(rawMessage, envelope.getPayload()).map(RustEvent.class::cast);
        } else if (PLAYER_DEATH_EVENT_TYPE.equalsIgnoreCase(envelope.getEventType())) {
            return parsePlayerDeath(rawMessage, envelope.getPayload()).map(RustEvent.class::cast);
        } else if (PLAYER_DISCONNECTED_EVENT_TYPE.equalsIgnoreCase(envelope.getEventType())) {
            return parsePlayerDisconnected(rawMessage, envelope.getPayload()).map(RustEvent.class::cast);
        } else if (PLAYER_KICKED_EVENT_TYPE.equalsIgnoreCase(envelope.getEventType())) {
            return parsePlayerKick(rawMessage, envelope.getPayload()).map(RustEvent.class::cast);
        } else if (PLAYER_RECOVERED_EVENT_TYPE.equalsIgnoreCase(envelope.getEventType())) {
            return parsePlayerLifecycle(rawMessage, envelope.getPayload())
                    .map(data -> new PlayerRecoveredEvent(data.steamId(), data.playerName()))
                    .map(RustEvent.class::cast);
        } else if (PLAYER_REPORTED_EVENT_TYPE.equalsIgnoreCase(envelope.getEventType())) {
            return parsePlayerReported(rawMessage, envelope.getPayload()).map(RustEvent.class::cast);
        } else if (PLAYER_RESPAWNED_EVENT_TYPE.equalsIgnoreCase(envelope.getEventType())) {
            return parsePlayerLifecycle(rawMessage, envelope.getPayload())
                    .map(data -> new PlayerRespawnedEvent(data.steamId(), data.playerName()))
                    .map(RustEvent.class::cast);
        } else if (PLAYER_UNBANNED_EVENT_TYPE.equalsIgnoreCase(envelope.getEventType())) {
            return parsePlayerUnban(rawMessage, envelope.getPayload()).map(RustEvent.class::cast);
        } else if (PLAYER_VIOLATION_EVENT_TYPE.equalsIgnoreCase(envelope.getEventType())) {
            return parsePlayerViolation(rawMessage, envelope.getPayload()).map(RustEvent.class::cast);
        } else if (PLAYER_WOUNDED_EVENT_TYPE.equalsIgnoreCase(envelope.getEventType())) {
            return parsePlayerLifecycle(rawMessage, envelope.getPayload())
                    .map(data -> new PlayerWoundedEvent(data.steamId(), data.playerName()))
                    .map(RustEvent.class::cast);
        } else if (SERVER_INITIALIZED_EVENT_TYPE.equalsIgnoreCase(envelope.getEventType())) {
            return Optional.of(new ServerInitializedEvent(event.getRconResponse().getServer()));
        } else if (SERVER_SAVE_EVENT_TYPE.equalsIgnoreCase(envelope.getEventType())) {
            return Optional.of(new SaveEvent(event.getRconResponse().getServer()));
        } else if (SERVER_SHUTDOWN_EVENT_TYPE.equalsIgnoreCase(envelope.getEventType())) {
            return Optional.of(new ServerShutdownEvent(event.getRconResponse().getServer()));
        } else if (TEAM_EVENT_TYPE.equalsIgnoreCase(envelope.getEventType())) {
            return parseTeam(rawMessage, envelope.getPayload()).map(RustEvent.class::cast);
        } else if (WORLD_EVENT_TYPE.equalsIgnoreCase(envelope.getEventType())) {
            return parseWorld(event, envelope.getPayload()).map(RustEvent.class::cast);
        }

        emitDiagnostic(
                UmodBridgeDiagnosticType.UNKNOWN_EVENT_TYPE,
                rawMessage,
                String.format("Unknown bridge event type: %s", envelope.getEventType())
        );
        return Optional.empty();
    }

    private Optional<PlayerChatEvent> parsePlayerChat(@NonNull String rawMessage, JsonNode payload) {
        if (payload == null || !payload.hasNonNull("playerName") || !payload.hasNonNull("message")) {
            emitDiagnostic(
                    UmodBridgeDiagnosticType.INVALID_PAYLOAD,
                    rawMessage,
                    "player.chat payload must contain playerName and message"
            );
            return Optional.empty();
        }

        final var steamId = text(payload, "steamId")
                .flatMap(SteamId64::parse)
                .orElse(null);
        final var playerName = PlayerName.ofNullable(payload.get("playerName").asText());
        final var message = payload.get("message").asText();
        final var chatChannel = parseChatChannel(rawMessage, payload);

        return Optional.of(new PlayerChatEvent(steamId, playerName, message, chatChannel));
    }

    private Optional<PlayerConnectedEvent> parsePlayerConnected(@NonNull String rawMessage, JsonNode payload) {
        if (payload == null
                || !payload.hasNonNull("steamId")
                || !payload.hasNonNull("playerName")
                || !payload.hasNonNull("ipAddress")) {
            emitDiagnostic(
                    UmodBridgeDiagnosticType.INVALID_PAYLOAD,
                    rawMessage,
                    "player.connected payload must contain steamId, playerName and ipAddress"
            );
            return Optional.empty();
        }

        final var steamId = SteamId64.parse(payload.get("steamId").asText());
        if (steamId.isEmpty()) {
            emitDiagnostic(UmodBridgeDiagnosticType.INVALID_PAYLOAD, rawMessage, "player.connected steamId is invalid");
            return Optional.empty();
        }

        return Optional.of(new PlayerConnectedEvent(
                steamId.get(),
                PlayerName.ofNullable(payload.get("playerName").asText()),
                OperatingSystems.UNKNOWN,
                payload.get("ipAddress").asText()
        ));
    }

    private Optional<PlayerDeathEvent> parsePlayerDeath(@NonNull String rawMessage, JsonNode payload) {
        if (payload == null) {
            emitDiagnostic(UmodBridgeDiagnosticType.INVALID_PAYLOAD, rawMessage, "player.death payload must not be null");
            return Optional.empty();
        }

        try {
            final var playerDeath = jsonMapper.fromJson(payload.toString(), PlayerDeathDTO.class);
            return Optional.of(playerDeathEventParser.mapPlayerDeathEvent().apply(playerDeath));
        } catch (Exception e) {
            emitDiagnostic(UmodBridgeDiagnosticType.INVALID_PAYLOAD, rawMessage, e.getMessage());
            return Optional.empty();
        }
    }

    private Optional<PlayerDisconnectedEvent> parsePlayerDisconnected(@NonNull String rawMessage, JsonNode payload) {
        if (payload == null || !payload.hasNonNull("steamId") || !payload.hasNonNull("reason")) {
            emitDiagnostic(
                    UmodBridgeDiagnosticType.INVALID_PAYLOAD,
                    rawMessage,
                    "player.disconnected payload must contain steamId and reason"
            );
            return Optional.empty();
        }

        final var steamId = SteamId64.parse(payload.get("steamId").asText());
        if (steamId.isEmpty()) {
            emitDiagnostic(UmodBridgeDiagnosticType.INVALID_PAYLOAD, rawMessage, "player.disconnected steamId is invalid");
            return Optional.empty();
        }

        return Optional.of(new PlayerDisconnectedEvent(
                steamId.get(),
                payload.get("reason").asText()
        ));
    }

    private Optional<PlayerReportedEvent> parsePlayerReported(@NonNull String rawMessage, JsonNode payload) {
        if (payload == null
                || !payload.hasNonNull("reporterSteamId")
                || !payload.hasNonNull("reporterName")
                || !payload.hasNonNull("targetName")
                || !payload.hasNonNull("subject")
                || !payload.hasNonNull("message")
                || !payload.hasNonNull("reportType")) {
            emitDiagnostic(
                    UmodBridgeDiagnosticType.INVALID_PAYLOAD,
                    rawMessage,
                    "player.reported payload must contain reporterSteamId, reporterName, targetName, subject, message and reportType"
            );
            return Optional.empty();
        }

        final var reporterSteamId = SteamId64.parse(payload.get("reporterSteamId").asText());
        if (reporterSteamId.isEmpty()) {
            emitDiagnostic(UmodBridgeDiagnosticType.INVALID_PAYLOAD, rawMessage, "player.reported reporterSteamId is invalid");
            return Optional.empty();
        }

        final var targetSteamId = text(payload, "targetSteamId").flatMap(SteamId64::parse).orElse(null);
        return Optional.of(new PlayerReportedEvent(
                reporterSteamId.get(),
                PlayerName.ofNullable(payload.get("reporterName").asText()),
                targetSteamId,
                PlayerName.ofNullable(payload.get("targetName").asText()),
                payload.get("subject").asText(),
                payload.get("message").asText(),
                payload.get("reportType").asText()
        ));
    }

    private Optional<PlayerViolationEvent> parsePlayerViolation(@NonNull String rawMessage, JsonNode payload) {
        if (payload == null
                || !payload.hasNonNull("steamId")
                || !payload.hasNonNull("playerName")
                || !payload.hasNonNull("violationType")
                || !payload.hasNonNull("amount")) {
            emitDiagnostic(
                    UmodBridgeDiagnosticType.INVALID_PAYLOAD,
                    rawMessage,
                    "player.violation payload must contain steamId, playerName, violationType and amount"
            );
            return Optional.empty();
        }

        final var steamId = SteamId64.parse(payload.get("steamId").asText());
        if (steamId.isEmpty()) {
            emitDiagnostic(UmodBridgeDiagnosticType.INVALID_PAYLOAD, rawMessage, "player.violation steamId is invalid");
            return Optional.empty();
        }

        return Optional.of(new PlayerViolationEvent(
                steamId.get(),
                PlayerName.ofNullable(payload.get("playerName").asText()),
                payload.get("violationType").asText(),
                new BigDecimal(payload.get("amount").asText())
        ));
    }

    private Optional<PlayerBannedEvent> parsePlayerBan(@NonNull String rawMessage, JsonNode payload) {
        return parsePlayerModeration(rawMessage, payload, PLAYER_BANNED_EVENT_TYPE, true)
                .map(data -> new PlayerBannedEvent(
                        data.steamId(),
                        data.playerName(),
                        data.ipAddress(),
                        data.reason(),
                        payload.get("expiry").asLong()
                ));
    }

    private Optional<PlayerKickedEvent> parsePlayerKick(@NonNull String rawMessage, JsonNode payload) {
        return parsePlayerModeration(rawMessage, payload, PLAYER_KICKED_EVENT_TYPE, true)
                .map(data -> new PlayerKickedEvent(
                        data.steamId(),
                        data.playerName(),
                        data.ipAddress(),
                        data.reason()
                ));
    }

    private Optional<PlayerUnbannedEvent> parsePlayerUnban(@NonNull String rawMessage, JsonNode payload) {
        return parsePlayerModeration(rawMessage, payload, PLAYER_UNBANNED_EVENT_TYPE, false)
                .map(data -> new PlayerUnbannedEvent(
                        data.steamId(),
                        data.playerName(),
                        data.ipAddress()
                ));
    }

    private Optional<PlayerModerationData> parsePlayerModeration(
            @NonNull String rawMessage,
            JsonNode payload,
            @NonNull String eventType,
            boolean requireReason
    ) {
        if (payload == null
                || !payload.hasNonNull("steamId")
                || !payload.hasNonNull("playerName")
                || (requireReason && !payload.hasNonNull("reason"))
                || (PLAYER_BANNED_EVENT_TYPE.equals(eventType) && !payload.hasNonNull("expiry"))) {
            emitDiagnostic(
                    UmodBridgeDiagnosticType.INVALID_PAYLOAD,
                    rawMessage,
                    String.format("%s payload must contain steamId, playerName%s%s",
                            eventType,
                            requireReason ? ", reason" : "",
                            PLAYER_BANNED_EVENT_TYPE.equals(eventType) ? " and expiry" : "")
            );
            return Optional.empty();
        }

        final var steamId = SteamId64.parse(payload.get("steamId").asText());
        if (steamId.isEmpty()) {
            emitDiagnostic(UmodBridgeDiagnosticType.INVALID_PAYLOAD, rawMessage, eventType + " steamId is invalid");
            return Optional.empty();
        }

        return Optional.of(new PlayerModerationData(
                steamId.get(),
                PlayerName.ofNullable(payload.get("playerName").asText()),
                text(payload, "ipAddress").orElse(""),
                text(payload, "reason").orElse("")
        ));
    }

    private Optional<TeamEvent> parseTeam(@NonNull String rawMessage, JsonNode payload) {
        if (payload == null
                || !payload.hasNonNull("teamId")
                || !payload.hasNonNull("leaderSteamId")
                || !payload.hasNonNull("teamEventType")) {
            emitDiagnostic(
                    UmodBridgeDiagnosticType.INVALID_PAYLOAD,
                    rawMessage,
                    "team payload must contain teamId, leaderSteamId and teamEventType"
            );
            return Optional.empty();
        }

        try {
            return Optional.of(new TeamEvent(
                    payload.get("teamId").asLong(),
                    payload.get("leaderSteamId").asLong(),
                    TeamEventTypes.valueOf(payload.get("teamEventType").asText().toUpperCase()),
                    text(payload, "actorSteamId").flatMap(SteamId64::parse).orElse(null),
                    PlayerName.ofNullable(text(payload, "actorName").orElse(null)),
                    text(payload, "targetSteamId").flatMap(SteamId64::parse).orElse(null),
                    parseStringSet(payload.get("members"))
            ));
        } catch (IllegalArgumentException e) {
            emitDiagnostic(UmodBridgeDiagnosticType.INVALID_PAYLOAD, rawMessage, e.getMessage());
            return Optional.empty();
        }
    }

    private Optional<ExplosiveUseEvent> parseExplosiveUse(@NonNull String rawMessage, JsonNode payload) {
        if (payload == null
                || !payload.hasNonNull("steamId")
                || !payload.hasNonNull("playerName")
                || !payload.hasNonNull("explosiveUseType")) {
            emitDiagnostic(
                    UmodBridgeDiagnosticType.INVALID_PAYLOAD,
                    rawMessage,
                    "explosive.use payload must contain steamId, playerName and explosiveUseType"
            );
            return Optional.empty();
        }

        final var steamId = SteamId64.parse(payload.get("steamId").asText());
        if (steamId.isEmpty()) {
            emitDiagnostic(UmodBridgeDiagnosticType.INVALID_PAYLOAD, rawMessage, "explosive.use steamId is invalid");
            return Optional.empty();
        }

        try {
            return Optional.of(new ExplosiveUseEvent(
                    steamId.get(),
                    PlayerName.ofNullable(payload.get("playerName").asText()),
                    ExplosiveUseTypes.valueOf(payload.get("explosiveUseType").asText().toUpperCase()),
                    text(payload, "weapon").orElse(""),
                    text(payload, "entity").orElse(""),
                    text(payload, "position").orElse("")
            ));
        } catch (IllegalArgumentException e) {
            emitDiagnostic(UmodBridgeDiagnosticType.INVALID_PAYLOAD, rawMessage, e.getMessage());
            return Optional.empty();
        }
    }

    private Optional<WorldEvent> parseWorld(@NonNull RconReceivedEvent event, JsonNode payload) {
        final var rawMessage = event.getRconResponse().getMessage();
        if (payload == null || !payload.hasNonNull("worldEvent")) {
            emitDiagnostic(
                    UmodBridgeDiagnosticType.INVALID_PAYLOAD,
                    rawMessage,
                    "world.event payload must contain worldEvent"
            );
            return Optional.empty();
        }

        try {
            return Optional.of(new WorldEvent(
                    event.getRconResponse().getServer(),
                    WorldEvents.valueOf(payload.get("worldEvent").asText().toUpperCase()),
                    parseAttributes(payload.get("attributes"))
            ));
        } catch (IllegalArgumentException e) {
            emitDiagnostic(UmodBridgeDiagnosticType.INVALID_PAYLOAD, rawMessage, e.getMessage());
            return Optional.empty();
        }
    }

    private Optional<PlayerLifecycleData> parsePlayerLifecycle(@NonNull String rawMessage, JsonNode payload) {
        if (payload == null || !payload.hasNonNull("steamId") || !payload.hasNonNull("playerName")) {
            emitDiagnostic(
                    UmodBridgeDiagnosticType.INVALID_PAYLOAD,
                    rawMessage,
                    "player lifecycle payload must contain steamId and playerName"
            );
            return Optional.empty();
        }

        final var steamId = SteamId64.parse(payload.get("steamId").asText());
        if (steamId.isEmpty()) {
            emitDiagnostic(UmodBridgeDiagnosticType.INVALID_PAYLOAD, rawMessage, "player lifecycle steamId is invalid");
            return Optional.empty();
        }

        return Optional.of(new PlayerLifecycleData(
                steamId.get(),
                PlayerName.ofNullable(payload.get("playerName").asText())
        ));
    }

    private record PlayerLifecycleData(SteamId64 steamId, PlayerName playerName) {
    }

    private record PlayerModerationData(SteamId64 steamId, PlayerName playerName, String ipAddress, String reason) {
    }

    private Optional<String> text(JsonNode payload, @NonNull String fieldName) {
        if (payload != null && payload.hasNonNull(fieldName)) {
            return Optional.of(payload.get(fieldName).asText());
        }

        return Optional.empty();
    }

    private Set<String> parseStringSet(JsonNode value) {
        final var values = new HashSet<String>();
        if (value != null && value.isArray()) {
            value.forEach(member -> values.add(member.asText()));
        }

        return values;
    }

    private Map<String, String> parseAttributes(JsonNode value) {
        final var attributes = new HashMap<String, String>();
        if (value != null && value.isObject()) {
            value.fields().forEachRemaining(entry -> attributes.put(entry.getKey(), entry.getValue().asText()));
        }

        return attributes;
    }

    private ChatChannels parseChatChannel(@NonNull String rawMessage, JsonNode payload) {
        try {
            return text(payload, "chatChannel")
                    .map(String::toUpperCase)
                    .map(ChatChannels::valueOf)
                    .orElse(ChatChannels.DEFAULT);
        } catch (IllegalArgumentException e) {
            emitDiagnostic(UmodBridgeDiagnosticType.INVALID_PAYLOAD, rawMessage, e.getMessage());
            return ChatChannels.DEFAULT;
        }
    }

    private void emitDiagnostic(
            @NonNull UmodBridgeDiagnosticType diagnosticType,
            @NonNull String rawMessage,
            String details
    ) {
        log.debug("uMod bridge diagnostic {}: {}", diagnosticType, details);
        eventBus.post(new UmodBridgeDiagnosticEvent(diagnosticType, rawMessage, details));
    }

    private void logBridgeEnvelope(@NonNull RconReceivedEvent event, @NonNull UmodBridgeEnvelope envelope) {
        log.info(
                "Received uMod bridge event {} from {} ({}) payload={}",
                envelope.getEventType(),
                event.getClientName(),
                envelope.getEventId(),
                envelope.getPayload()
        );
    }
}
