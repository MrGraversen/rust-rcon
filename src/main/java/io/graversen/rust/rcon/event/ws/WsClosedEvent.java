package io.graversen.rust.rcon.event.ws;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
public class WsClosedEvent extends WsEvent {
    private final @NonNull Integer code;
    private final @NonNull String reason;

    public WsClosedEvent(@NonNull String serverUri, @NonNull Integer code, @NonNull String reason) {
        super(serverUri);
        this.code = code;
        this.reason = reason;
    }
}
