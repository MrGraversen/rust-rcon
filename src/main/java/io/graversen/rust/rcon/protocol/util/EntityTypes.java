package io.graversen.rust.rcon.protocol.util;

import lombok.NonNull;

import java.util.Objects;

public enum EntityTypes {
    PLAYER,
    ANIMAL,
    TRAP,
    SCIENTIST,
    SENTRY,
    BRADLEY,
    UNDERWATER_DWELLER,
    UNKNOWN;

    public static EntityTypes parse(String killerEntityType) {
        if (Objects.isNull(killerEntityType)) {
            return UNKNOWN;
        }

        if (EntityTypes.isAnimal(killerEntityType)) {
            return ANIMAL;
        } else if (EntityTypes.isPlayer(killerEntityType)) {
            return PLAYER;
        } else if (EntityTypes.isTrap(killerEntityType)) {
            return TRAP;
        } else if (EntityTypes.isScientist(killerEntityType)) {
            return SCIENTIST;
        } else if (EntityTypes.isUnderwaterDweller(killerEntityType)) {
            return UNDERWATER_DWELLER;
        } else if (EntityTypes.isSentry(killerEntityType)) {
            return SENTRY;
        } else if (EntityTypes.isBradley(killerEntityType)) {
            return BRADLEY;
        } else {
            return UNKNOWN;
        }
    }

    public static boolean isAnimal(@NonNull String killerEntityType) {
        return killerEntityType.equalsIgnoreCase("animal") || Animals.parse(killerEntityType) != Animals.UNKNOWN;
    }

    public static boolean isTrap(@NonNull String killerEntityType) {
        return killerEntityType.equalsIgnoreCase("trap") || Traps.parse(killerEntityType) != Traps.UNKNOWN;
    }

    public static boolean isPlayer(@NonNull String killerEntityType) {
        return killerEntityType.equalsIgnoreCase("player");
    }

    public static boolean isScientist(@NonNull String killerEntityType) {
        return killerEntityType.equalsIgnoreCase("scientist");
    }

    public static boolean isUnderwaterDweller(@NonNull String killerEntityType) {
        return killerEntityType.equalsIgnoreCase("underwaterdweller");
    }

    public static boolean isSentry(@NonNull String killerEntityType) {
        return killerEntityType.equalsIgnoreCase("sentry");
    }

    public static boolean isBradley(@NonNull String killerEntityType) {
        return killerEntityType.equalsIgnoreCase("bradley");
    }

    public static boolean isNonPlayerCharacter(@NonNull String entityType) {
        return EntityTypes.isScientist(entityType)
                || EntityTypes.isAnimal(entityType)
                || EntityTypes.isSentry(entityType)
                || EntityTypes.isUnderwaterDweller(entityType)
                || EntityTypes.isBradley(entityType);
    }
}
