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
import io.graversen.rust.rcon.event.player.PlayerRespawnedEvent;
import io.graversen.rust.rcon.event.player.PlayerUnbannedEvent;
import io.graversen.rust.rcon.event.player.PlayerWoundedEvent;
import io.graversen.rust.rcon.event.rcon.RconReceivedEvent;
import io.graversen.rust.rcon.event.server.SaveEvent;
import io.graversen.rust.rcon.event.server.ServerInitializedEvent;
import io.graversen.rust.rcon.event.server.ServerShutdownEvent;
import io.graversen.rust.rcon.protocol.util.ChatChannels;
import io.graversen.rust.rcon.protocol.util.OperatingSystems;
import io.graversen.rust.rcon.protocol.util.PlayerName;
import io.graversen.rust.rcon.protocol.util.SteamId64;
import io.graversen.rust.rcon.util.DefaultJsonMapper;
import io.graversen.rust.rcon.util.JsonMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
public class UmodBridgeRustEventService extends BaseEventHandler implements RustEventService {
    public static final String BRIDGE_PREFIX = "[rust-rcon]";

    private static final String PLAYER_BANNED_EVENT_TYPE = "player.banned";
    private static final String PLAYER_CHAT_EVENT_TYPE = "player.chat";
    private static final String PLAYER_CONNECTED_EVENT_TYPE = "player.connected";
    private static final String PLAYER_DEATH_EVENT_TYPE = "player.death";
    private static final String PLAYER_DISCONNECTED_EVENT_TYPE = "player.disconnected";
    private static final String PLAYER_KICKED_EVENT_TYPE = "player.kicked";
    private static final String PLAYER_RECOVERED_EVENT_TYPE = "player.recovered";
    private static final String PLAYER_RESPAWNED_EVENT_TYPE = "player.respawned";
    private static final String PLAYER_UNBANNED_EVENT_TYPE = "player.unbanned";
    private static final String PLAYER_WOUNDED_EVENT_TYPE = "player.wounded";
    private static final String SERVER_INITIALIZED_EVENT_TYPE = "server.initialized";
    private static final String SERVER_SAVE_EVENT_TYPE = "server.save";
    private static final String SERVER_SHUTDOWN_EVENT_TYPE = "server.shutdown";

    private final @NonNull EventBus eventBus;
    private final @NonNull JsonMapper jsonMapper = new DefaultJsonMapper();
    private final @NonNull PlayerDeathEventParser playerDeathEventParser = new PlayerDeathEventParser();

    @Subscribe
    @Override
    public void onRconReceived(@NonNull RconReceivedEvent event) {
        final var message = event.getRconResponse().getMessage();
        if (!message.startsWith(BRIDGE_PREFIX)) {
            return;
        }

        parseEnvelope(message)
                .flatMap(envelope -> parseEvent(event, envelope))
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
                        PlayerBannedEvent.class,
                        PlayerChatEvent.class,
                        PlayerConnectedEvent.class,
                        PlayerDeathEvent.class,
                        PlayerDisconnectedEvent.class,
                        PlayerKickedEvent.class,
                        PlayerRecoveredEvent.class,
                        PlayerRespawnedEvent.class,
                        PlayerUnbannedEvent.class,
                        PlayerWoundedEvent.class,
                        SaveEvent.class,
                        ServerInitializedEvent.class,
                        ServerShutdownEvent.class,
                        UmodBridgeDiagnosticEvent.class
                )
        );
    }

    private Optional<UmodBridgeEnvelope> parseEnvelope(@NonNull String rawMessage) {
        final var json = rawMessage.substring(BRIDGE_PREFIX.length()).trim();
        try {
            final var envelope = jsonMapper.fromJson(json, UmodBridgeEnvelope.class);
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

    private Optional<RustEvent> parseEvent(@NonNull RconReceivedEvent event, @NonNull UmodBridgeEnvelope envelope) {
        final var rawMessage = event.getRconResponse().getMessage();
        if (PLAYER_BANNED_EVENT_TYPE.equalsIgnoreCase(envelope.getEventType())) {
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
        } else if (PLAYER_RESPAWNED_EVENT_TYPE.equalsIgnoreCase(envelope.getEventType())) {
            return parsePlayerLifecycle(rawMessage, envelope.getPayload())
                    .map(data -> new PlayerRespawnedEvent(data.steamId(), data.playerName()))
                    .map(RustEvent.class::cast);
        } else if (PLAYER_UNBANNED_EVENT_TYPE.equalsIgnoreCase(envelope.getEventType())) {
            return parsePlayerUnban(rawMessage, envelope.getPayload()).map(RustEvent.class::cast);
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
}
