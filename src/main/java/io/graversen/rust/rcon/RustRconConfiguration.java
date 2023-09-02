package io.graversen.rust.rcon;

import lombok.NonNull;
import lombok.Value;

@Value
public class RustRconConfiguration {
    @NonNull String hostname;
    @NonNull Integer port;
    @NonNull String password;
}
