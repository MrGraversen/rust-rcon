package io.graversen.rust.rcon.event.server;

import io.graversen.rust.rcon.RustServer;
import io.graversen.rust.rcon.event.RustEvent;
import lombok.*;

@Getter
@ToString(callSuper = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class ServerEvent extends RustEvent {
    private final @NonNull RustServer server;
}
