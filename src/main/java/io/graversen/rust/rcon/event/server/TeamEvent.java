package io.graversen.rust.rcon.event.server;

import io.graversen.rust.rcon.event.RustEvent;
import io.graversen.rust.rcon.protocol.util.PlayerName;
import io.graversen.rust.rcon.protocol.util.SteamId64;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import javax.annotation.Nullable;
import java.util.Set;

@Getter
@ToString
public class TeamEvent extends RustEvent {
    private final long teamId;
    private final long leaderSteamId;
    private final @NonNull TeamEventTypes teamEventType;
    private final @Nullable SteamId64 actorSteamId;
    private final @NonNull PlayerName actorName;
    private final @Nullable SteamId64 targetSteamId;
    private final @NonNull Set<String> members;

    public TeamEvent(
            long teamId,
            long leaderSteamId,
            @NonNull TeamEventTypes teamEventType,
            @Nullable SteamId64 actorSteamId,
            @NonNull PlayerName actorName,
            @Nullable SteamId64 targetSteamId,
            @NonNull Set<String> members
    ) {
        this.teamId = teamId;
        this.leaderSteamId = leaderSteamId;
        this.teamEventType = teamEventType;
        this.actorSteamId = actorSteamId;
        this.actorName = actorName;
        this.targetSteamId = targetSteamId;
        this.members = members;
    }
}
