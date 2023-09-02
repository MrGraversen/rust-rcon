package io.graversen.rust.rcon.protocol.util;

import io.graversen.rust.rcon.protocol.DefaultPlaceholders;
import lombok.NonNull;
import lombok.Value;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Supplier;

@Value
public class PlayerName implements Supplier<String> {
    @NonNull String playerName;

    public static PlayerName ofNullable(@Nullable String playerName) {
        return new PlayerName(Objects.requireNonNullElse(playerName, DefaultPlaceholders.DEFAULT_PLAYER_NAME_STRING));
    }

    @Override
    public String get() {
        return playerName;
    }
}
