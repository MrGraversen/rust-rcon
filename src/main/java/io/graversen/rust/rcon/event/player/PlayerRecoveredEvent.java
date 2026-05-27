package io.graversen.rust.rcon.event.player;

import io.graversen.rust.rcon.protocol.util.PlayerName;
import io.graversen.rust.rcon.protocol.util.SteamId64;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
public class PlayerRecoveredEvent extends PlayerEvent {
    private final @NonNull PlayerName playerName;

    public PlayerRecoveredEvent(@NonNull SteamId64 steamId, @NonNull PlayerName playerName) {
        super(steamId);
        this.playerName = playerName;
    }
}
