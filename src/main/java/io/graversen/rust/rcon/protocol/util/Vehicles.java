package io.graversen.rust.rcon.protocol.util;

import java.util.Objects;

public enum Vehicles {
    MINICOPTER,
    MOTORROWBOAT,
    RHIB,
    UNKNOWN;

    public static Vehicles parse(String string) {
        if (Objects.isNull(string)) {
            return UNKNOWN;
        }

        try {
            return Vehicles.valueOf(string.toUpperCase());
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }
}
