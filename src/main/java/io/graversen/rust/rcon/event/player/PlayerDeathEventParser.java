package io.graversen.rust.rcon.event.player;

import io.graversen.rust.rcon.RustRconResponse;
import io.graversen.rust.rcon.configuration.Configuration;
import io.graversen.rust.rcon.event.BaseRustEventParser;
import io.graversen.rust.rcon.protocol.util.*;
import io.graversen.rust.rcon.util.DefaultJsonMapper;
import io.graversen.rust.rcon.util.JsonMapper;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class PlayerDeathEventParser extends BaseRustEventParser<PlayerDeathEvent> {
    private static final String MESSAGE_PREFIX = String.format("[%s]", Configuration.UNDERTAKER_PLUGIN_NAME);

    private JsonMapper jsonMapper;

    @Override
    public boolean supports(@NonNull RustRconResponse payload) {
        return payload.getMessage().startsWith(MESSAGE_PREFIX);
    }

    @Override
    protected Function<RustRconResponse, Optional<PlayerDeathEvent>> eventParser() {
        return rconResponse -> {
            final var message = rconResponse.getMessage();
            final var trimmedMessage = message.substring(MESSAGE_PREFIX.length()).trim();
            return mapPlayerDeath().apply(trimmedMessage).map(mapPlayerDeathEvent());
        };
    }

    @Override
    public Class<PlayerDeathEvent> eventClass() {
        return PlayerDeathEvent.class;
    }

    protected JsonMapper jsonMapper() {
        if (jsonMapper == null) {
            jsonMapper = new DefaultJsonMapper();
        }

        return jsonMapper;
    }

    Function<String, Optional<PlayerDeathDTO>> mapPlayerDeath() {
        return message -> {
            try {
                final var playerDeath = jsonMapper().fromJson(message, PlayerDeathDTO.class);
                return Optional.ofNullable(playerDeath);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return Optional.empty();
            }
        };
    }

    Function<PlayerDeathDTO, PlayerDeathEvent> mapPlayerDeathEvent() {
        return playerDeath -> {
            final var attachments = playerDeath.getAttachments() == null
                    ? Set.<String>of()
                    : Arrays.stream(playerDeath.getAttachments().split(",")).map(String::trim).collect(Collectors.toUnmodifiableSet());

            final var victimEntity = EntityTypes.parse(playerDeath.getVictimEntityType());
            final var killerEntity = EntityTypes.parse(playerDeath.getKillerEntityType());
            final var bodyPart = BodyParts.parse(playerDeath.getBodyPart());
            final var combatType = resolveCombatType(playerDeath);
            final var damageType = DamageTypes.parse(playerDeath.getDamageType());
            final var distance = playerDeath.getDistance().replaceAll("[^\\d.]", "");

            final var playerEventSteamId = SteamId64.parse(playerDeath.getKillerId())
                    .or(() -> SteamId64.parse(playerDeath.getVictimId()))
                    .orElseThrow(() -> new IllegalArgumentException(String.format("Could not resolve SteamID from: %s", playerDeath)));

            return new PlayerDeathEvent(
                    playerEventSteamId,
                    playerDeath.getVictim(),
                    victimEntity,
                    SteamId64.parse(playerDeath.getVictimId()).orElse(null),
                    Objects.isNull(playerDeath.getOwner()) ? playerDeath.getKiller() : playerDeath.getOwner(),
                    killerEntity,
                    bodyPart,
                    new BigDecimal(distance),
                    new BigDecimal(Objects.requireNonNullElse(playerDeath.getHealth(), "0")),
                    Objects.isNull(playerDeath.getOwner()) ? playerDeath.getWeapon() : playerDeath.getKiller(),
                    attachments,
                    combatType,
                    damageType
            );
        };
    }

    CombatTypes resolveCombatType(@NonNull PlayerDeathDTO playerDeath) {
        if (playerDeath.getKillerId() != null && playerDeath.getVictimId() != null) {
            if (Objects.equals(playerDeath.getKillerId(), playerDeath.getVictimId())) {
                return CombatTypes.SUICIDE;
            }
        }

        return CombatTypes.resolve(playerDeath.getKillerEntityType(), playerDeath.getVictimEntityType());
    }
}
