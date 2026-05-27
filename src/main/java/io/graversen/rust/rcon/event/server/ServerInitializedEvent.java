package io.graversen.rust.rcon.event.server;

import io.graversen.rust.rcon.RustServer;
import lombok.NonNull;
import lombok.ToString;

@ToString(callSuper = true)
public class ServerInitializedEvent extends ServerEvent {
    public ServerInitializedEvent(@NonNull RustServer server) {
        super(server);
    }
}
