package io.graversen.rust.rcon.event.player;

import io.graversen.rust.rcon.protocol.util.PlayerName;
import io.graversen.rust.rcon.protocol.util.SteamId64;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
public class PlayerChatEvent extends PlayerEvent {
    private final @NonNull PlayerName playerName;
    private final @NonNull String message;

    public PlayerChatEvent(@NonNull SteamId64 steamId, @NonNull PlayerName playerName, @NonNull String message) {
        super(steamId);
        this.playerName = playerName;
        this.message = message;
    }
}
