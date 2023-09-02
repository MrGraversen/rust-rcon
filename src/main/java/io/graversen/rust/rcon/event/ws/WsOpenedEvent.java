package io.graversen.rust.rcon.event.ws;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
public class WsOpenedEvent extends WsEvent {
    public WsOpenedEvent(@NonNull String serverUri) {
        super(serverUri);
    }
}
