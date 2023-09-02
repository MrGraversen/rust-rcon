package io.graversen.rust.rcon.event.player;

import io.graversen.rust.rcon.event.RustEvent;
import io.graversen.rust.rcon.protocol.util.SteamId64;
import lombok.*;

@Getter
@ToString
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class PlayerEvent extends RustEvent {
    private final @NonNull SteamId64 steamId;
}
