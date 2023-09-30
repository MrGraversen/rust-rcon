package io.graversen.rust.rcon;

import io.graversen.rust.rcon.protocol.util.PlayerName;
import io.graversen.rust.rcon.protocol.util.SteamId64;
import io.graversen.rust.rcon.util.CommonUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.ZonedDateTime;

@Getter
@EqualsAndHashCode(callSuper = true)
public class FullRustPlayer extends RustPlayer {
    private final @NonNull String ping;
    private final @NonNull String ipAddress;
    private final @NonNull Duration connectedDuration;
    private final @NonNull BigDecimal health;

    public FullRustPlayer(
            @NonNull SteamId64 steamId,
            @NonNull PlayerName playerName,
            @NonNull String ping,
            @NonNull String ipAddress,
            @NonNull Duration connectedDuration,
            @NonNull BigDecimal health
    ) {
        super(steamId, playerName);
        this.ping = ping;
        this.ipAddress = ipAddress;
        this.connectedDuration = connectedDuration;
        this.health = health;
    }

    public ZonedDateTime connectedAt() {
        return CommonUtils.now().minus(connectedDuration);
    }
}
