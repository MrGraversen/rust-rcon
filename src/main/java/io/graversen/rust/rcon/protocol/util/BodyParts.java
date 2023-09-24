package io.graversen.rust.rcon.protocol.util;

import java.util.Objects;

public enum BodyParts {
    HEAD,
    BODY,
    LEG,
    ARM,
    HAND,
    STOMACH,
    CHEST,
    UNKNOWN;

    public static BodyParts parse(String bodyPart) {
        if (Objects.isNull(bodyPart)) {
            return UNKNOWN;
        }

        try {
            return BodyParts.valueOf(bodyPart.toUpperCase());
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }
}
