package io.graversen.rust.rcon.event.player;

import io.graversen.rust.rcon.protocol.util.PlayerName;
import io.graversen.rust.rcon.protocol.util.SteamId64;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@ToString(callSuper = true)
public class PlayerViolationEvent extends PlayerEvent {
    private final @NonNull PlayerName playerName;
    private final @NonNull String violationType;
    private final @NonNull BigDecimal amount;

    public PlayerViolationEvent(
            @NonNull SteamId64 steamId,
            @NonNull PlayerName playerName,
            @NonNull String violationType,
            @NonNull BigDecimal amount
    ) {
        super(steamId);
        this.playerName = playerName;
        this.violationType = violationType;
        this.amount = amount;
    }
}
