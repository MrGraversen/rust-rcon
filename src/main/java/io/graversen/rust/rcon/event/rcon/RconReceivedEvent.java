package io.graversen.rust.rcon.event.rcon;

import io.graversen.rust.rcon.event.RustEvent;
import io.graversen.rust.rcon.RustRconResponse;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public class RconReceivedEvent extends RustEvent {
    private final @NonNull String clientName;
    private final @NonNull RustRconResponse rconResponse;
}
