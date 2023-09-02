package io.graversen.rust.rcon.event.ws;

import io.graversen.rust.rcon.event.Event;
import lombok.*;

@Getter
@ToString
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class WsEvent extends Event {
    private final @NonNull String serverUri;
}
