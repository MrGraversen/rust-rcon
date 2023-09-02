package io.graversen.rust.rcon.event.player;

import io.graversen.rust.rcon.protocol.util.SteamId64;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
public class PlayerDisconnectedEvent extends PlayerEvent {
    private final @NonNull String reason;

    PlayerDisconnectedEvent(@NonNull SteamId64 steamId, @NonNull String reason) {
        super(steamId);
        this.reason = reason;
    }
}
