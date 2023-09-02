package io.graversen.rust.rcon.protocol.util;

import lombok.NonNull;

import java.util.Objects;

public enum EntityTypes {
    PLAYER,
    ANIMAL,
    TRAP,
    SCIENTIST,
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

    public static boolean isNonPlayerCharacter(@NonNull String killerEntityType) {
        return EntityTypes.isScientist(killerEntityType) || EntityTypes.isAnimal(killerEntityType);
    }
}
