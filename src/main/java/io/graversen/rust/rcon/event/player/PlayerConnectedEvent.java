package io.graversen.rust.rcon.event.player;

import io.graversen.rust.rcon.protocol.util.OperatingSystems;
import io.graversen.rust.rcon.protocol.util.PlayerName;
import io.graversen.rust.rcon.protocol.util.SteamId64;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
public class PlayerConnectedEvent extends PlayerEvent {
    private final @NonNull PlayerName playerName;
    private final @NonNull OperatingSystems operatingSystem;
    private final @NonNull String ipAddress;

    PlayerConnectedEvent(
            @NonNull SteamId64 steamId,
            @NonNull PlayerName playerName,
            @NonNull OperatingSystems operatingSystem,
            @NonNull String ipAddress
    ) {
        super(steamId);
        this.playerName = playerName;
        this.operatingSystem = operatingSystem;
        this.ipAddress = ipAddress;
    }
}
