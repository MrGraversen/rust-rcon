package io.graversen.rust.rcon.event.server;

import io.graversen.rust.rcon.event.RustEvent;
import io.graversen.rust.rcon.protocol.util.PlayerName;
import io.graversen.rust.rcon.protocol.util.SteamId64;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Getter
@ToString
public class ExplosiveUseEvent extends RustEvent {
    private final @NonNull SteamId64 steamId;
    private final @NonNull PlayerName playerName;
    private final @NonNull ExplosiveUseTypes explosiveUseType;
    private final @NonNull String weapon;
    private final @NonNull String entity;
    private final @NonNull String position;

    public ExplosiveUseEvent(
            @NonNull SteamId64 steamId,
            @NonNull PlayerName playerName,
            @NonNull ExplosiveUseTypes explosiveUseType,
            @NonNull String weapon,
            @NonNull String entity,
            @NonNull String position
    ) {
        this.steamId = steamId;
        this.playerName = playerName;
        this.explosiveUseType = explosiveUseType;
        this.weapon = weapon;
        this.entity = entity;
        this.position = position;
    }
}
