package io.graversen.rust.rcon.protocol.util;

import java.util.Objects;

public enum CombatTypes {
    PVP,
    PVE,
    SUICIDE,
    TRAP,
    UNKNOWN;

    public static CombatTypes resolve(String killerEntityType, String victimEntityType) {
        if (Objects.isNull(killerEntityType) || Objects.isNull(victimEntityType)) {
            return CombatTypes.UNKNOWN;
        }

        if (EntityTypes.isPlayer(killerEntityType) && EntityTypes.isPlayer(victimEntityType)) {
            return CombatTypes.PVP;
        } else if ((EntityTypes.isPlayer(killerEntityType) && EntityTypes.isNonPlayerCharacter(victimEntityType))
                || (EntityTypes.isNonPlayerCharacter(killerEntityType) && EntityTypes.isPlayer(victimEntityType))) {
            return CombatTypes.PVE;
        } else if (EntityTypes.isTrap(killerEntityType)) {
            return CombatTypes.TRAP;
        } else {
            return CombatTypes.UNKNOWN;
        }
    }
}
