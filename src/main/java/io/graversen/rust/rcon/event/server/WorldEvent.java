package io.graversen.rust.rcon.event.server;

import io.graversen.rust.rcon.RustServer;
import io.graversen.rust.rcon.protocol.util.WorldEvents;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import java.util.Map;

@Getter
@ToString(callSuper = true)
public class WorldEvent extends ServerEvent {
    private final @NonNull WorldEvents event;
    private final @NonNull Map<String, String> attributes;

    public WorldEvent(@NonNull RustServer server, @NonNull WorldEvents event) {
        this(server, event, Map.of());
    }

    public WorldEvent(@NonNull RustServer server, @NonNull WorldEvents event, @NonNull Map<String, String> attributes) {
        super(server);
        this.event = event;
        this.attributes = Map.copyOf(attributes);
    }
}
