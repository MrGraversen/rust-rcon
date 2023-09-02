package io.graversen.rust.rcon.event.ws;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
public class WsMessageEvent extends WsEvent {
    private final @NonNull String message;

    public WsMessageEvent(@NonNull String serverUri, @NonNull String message) {
        super(serverUri);
        this.message = message;
    }
}
