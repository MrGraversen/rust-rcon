package io.graversen.rust.rcon.protocol.util;

import lombok.NonNull;
import lombok.Value;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Supplier;

@Value
public class SteamId64 implements Supplier<String> {
    public static final int LENGTH = 17;

    @NonNull String value;

    public static Optional<SteamId64> parse(@Nullable String value) {
        if (value == null) {
            return Optional.empty();
        }

        if (value.length() != LENGTH) {
            return Optional.empty();
        }

        return Optional.of(new SteamId64(value));
    }

    public static SteamId64 parseOrFail(@Nullable String value) {
        return SteamId64.parse(value).orElseThrow(() -> new IllegalArgumentException(String.format("Could not parse Steam ID: %s", value)));
    }

    @Override
    public String get() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
