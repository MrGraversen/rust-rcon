package io.graversen.rust.rcon;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.ZonedDateTime;

public interface RustDiagnostics {
    ZonedDateTime lastUpdatedAt();

    Duration uptime();

    ZonedDateTime serverWipedAt();

    String version();

    String protocol();

    Integer maxPlayers();

    Integer currentPlayers();

    ZonedDateTime gameClock();

    BigDecimal framerate();
}
