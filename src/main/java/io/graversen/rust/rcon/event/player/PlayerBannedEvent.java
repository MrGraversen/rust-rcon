package io.graversen.rust.rcon.event.player;

import io.graversen.rust.rcon.protocol.util.PlayerName;
import io.graversen.rust.rcon.protocol.util.SteamId64;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
public class PlayerBannedEvent extends PlayerEvent {
    private final @NonNull PlayerName playerName;
    private final @NonNull String ipAddress;
    private final @NonNull String reason;
    private final long expiry;

    public PlayerBannedEvent(
            @NonNull SteamId64 steamId,
            @NonNull PlayerName playerName,
            @NonNull String ipAddress,
            @NonNull String reason,
            long expiry
    ) {
        super(steamId);
        this.playerName = playerName;
        this.ipAddress = ipAddress;
        this.reason = reason;
        this.expiry = expiry;
    }
}
