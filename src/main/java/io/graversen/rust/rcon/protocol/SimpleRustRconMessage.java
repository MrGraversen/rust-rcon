package io.graversen.rust.rcon.protocol;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class SimpleRustRconMessage implements RustRconMessage {
    @NonNull String message;

    @Override
    public String get() {
        return message;
    }
}
