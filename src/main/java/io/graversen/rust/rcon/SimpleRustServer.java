package io.graversen.rust.rcon;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class SimpleRustServer implements RustServer {
    @NonNull String name;
    @NonNull String serverUri;
}
