package io.graversen.rust.rcon.event;

import lombok.NonNull;

public abstract class BaseEventHandler {
    protected final void handleEvent(@NonNull Event event) {
        event.registerEventHandler(this);
    }
}
