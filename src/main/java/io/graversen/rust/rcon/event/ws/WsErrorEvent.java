package io.graversen.rust.rcon.event.ws;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
public class WsErrorEvent extends WsEvent {
    private final @NonNull Exception exception;

    public WsErrorEvent(@NonNull String serverUri, @NonNull Exception exception) {
        super(serverUri);
        this.exception = exception;
    }
}
