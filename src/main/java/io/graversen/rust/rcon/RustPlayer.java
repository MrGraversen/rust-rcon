package io.graversen.rust.rcon;

import io.graversen.rust.rcon.protocol.util.PlayerName;
import io.graversen.rust.rcon.protocol.util.SteamId64;
import io.graversen.rust.rcon.util.CommonUtils;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.ZonedDateTime;

@Value
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class RustPlayer {
    @NonNull SteamId64 steamId;
    @NonNull PlayerName playerName;
    @NonNull String ping;
    @NonNull String ipAddress;
    @NonNull Duration connectedDuration;
    @NonNull BigDecimal health;

    public ZonedDateTime connectedAt() {
        return CommonUtils.now().minus(connectedDuration);
    }
}
