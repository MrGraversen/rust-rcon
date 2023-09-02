package io.graversen.rust.rcon.event.player;

import io.graversen.rust.rcon.protocol.util.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@ToString(callSuper = true)
public class PlayerDeathEvent extends PlayerEvent {
    private final String victim;
    private final EntityTypes victimEntity;
    private final SteamId64 victimId;
    private final String killer;
    private final EntityTypes killerEntity;
    private final BodyParts bodyPart;
    private final BigDecimal distance;
    private final BigDecimal killerHealth;
    private final String weapon;
    private final Set<String> attachments;
    private final CombatTypes combatType;
    private final DamageTypes damageType;

    PlayerDeathEvent(
            @NonNull SteamId64 steamId,
            String victim,
            EntityTypes victimEntity,
            SteamId64 victimId,
            String killer,
            EntityTypes killerEntity,
            BodyParts bodyPart,
            BigDecimal distance,
            BigDecimal killerHealth,
            String weapon,
            Set<String> attachments,
            CombatTypes combatType,
            DamageTypes damageType
    ) {
        super(steamId);
        this.victim = victim;
        this.victimEntity = victimEntity;
        this.victimId = victimId;
        this.killer = killer;
        this.killerEntity = killerEntity;
        this.bodyPart = bodyPart;
        this.distance = distance;
        this.killerHealth = killerHealth;
        this.weapon = weapon;
        this.attachments = attachments;
        this.combatType = combatType;
        this.damageType = damageType;
    }
}
