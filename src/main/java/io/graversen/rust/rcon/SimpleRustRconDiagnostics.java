package io.graversen.rust.rcon;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.ZonedDateTime;

@Value
@Accessors(fluent = true)
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class SimpleRustRconDiagnostics implements RustDiagnostics {
    @NonNull ZonedDateTime lastUpdatedAt;
    @NonNull Duration uptime;
    @NonNull ZonedDateTime serverWipedAt;
    @NonNull String version;
    @NonNull String protocol;
    @NonNull Integer maxPlayers;
    @NonNull Integer currentPlayers;
    @NonNull ZonedDateTime gameClock;
    @NonNull BigDecimal framerate;
}
