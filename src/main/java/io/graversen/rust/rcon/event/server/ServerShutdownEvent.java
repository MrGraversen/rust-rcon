package io.graversen.rust.rcon.event.server;

import io.graversen.rust.rcon.RustServer;
import lombok.NonNull;
import lombok.ToString;

@ToString(callSuper = true)
public class ServerShutdownEvent extends ServerEvent {
    public ServerShutdownEvent(@NonNull RustServer server) {
        super(server);
    }
}
