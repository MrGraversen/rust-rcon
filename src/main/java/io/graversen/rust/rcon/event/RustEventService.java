package io.graversen.rust.rcon.event;

import io.graversen.rust.rcon.event.rcon.RconReceivedEvent;
import lombok.NonNull;

public interface RustEventService {
    void onRconReceived(@NonNull RconReceivedEvent event);

    void configure();
}
