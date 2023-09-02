package io.graversen.rust.rcon.event.server;

import io.graversen.rust.rcon.RustServer;
import io.graversen.rust.rcon.protocol.util.PlayerName;
import io.graversen.rust.rcon.protocol.util.SteamId64;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
public class EasyAntiCheatEvent extends ServerEvent {
    private final @NonNull SteamId64 steamId;
    private final @NonNull PlayerName playerName;
    private final @NonNull String reason;

    public EasyAntiCheatEvent(
            @NonNull RustServer server,
            @NonNull SteamId64 steamId,
            @NonNull PlayerName playerName,
            @NonNull String reason
    ) {
        super(server);
        this.steamId = steamId;
        this.playerName = playerName;
        this.reason = reason;
    }
}
