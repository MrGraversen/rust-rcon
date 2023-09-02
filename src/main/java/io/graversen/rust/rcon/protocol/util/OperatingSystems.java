package io.graversen.rust.rcon.protocol.util;

import lombok.NonNull;

public enum OperatingSystems {
    WINDOWS,
    LINUX,
    MAC_OS,
    UNKNOWN;

    public static OperatingSystems parse(@NonNull String operatingSystem) {
        return switch (operatingSystem.toLowerCase()) {
            case "windows" -> WINDOWS;
            case "osx" -> MAC_OS;
            case "linux" -> LINUX;
            default -> UNKNOWN;
        };
    }
}
