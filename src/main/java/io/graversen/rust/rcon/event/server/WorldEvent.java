package io.graversen.rust.rcon.event.server;

import io.graversen.rust.rcon.RustServer;
import io.graversen.rust.rcon.protocol.util.WorldEvents;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
public class WorldEvent extends ServerEvent {
    private final @NonNull WorldEvents event;

    WorldEvent(@NonNull RustServer server, @NonNull WorldEvents event) {
        super(server);
        this.event = event;
    }
}
