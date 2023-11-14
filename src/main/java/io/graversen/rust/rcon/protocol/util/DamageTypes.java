package io.graversen.rust.rcon.protocol.util;

import java.util.Objects;

public enum DamageTypes {
    BITE,
    BLUNT,
    BULLET,
    COLD,
    ELECTRIC,
    EXPLOSION,
    FALLING,
    RADIATION,
    SLASH,
    STAB,
    COLLISION,
    ANTIVEHICLE,
    SUICIDE,
    UNKNOWN;

    public static DamageTypes parse(String damageType) {
        if (Objects.isNull(damageType)) {
            return UNKNOWN;
        }

        try {
            return DamageTypes.valueOf(damageType.toUpperCase());
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }
}
