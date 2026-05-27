package io.graversen.rust.rcon;

import io.graversen.rust.rcon.event.RustEventSourceStrategy;
import lombok.NonNull;
import lombok.Value;

import java.util.Objects;

@Value
public class RustRconConfiguration {
    @NonNull String hostname;
    @NonNull Integer port;
    @NonNull String password;
    @NonNull RustEventSourceStrategy eventSourceStrategy;

    public RustRconConfiguration(
            @NonNull String hostname,
            @NonNull Integer port,
            @NonNull String password
    ) {
        this(hostname, port, password, RustEventSourceStrategy.RCON);
    }

    public RustRconConfiguration(
            @NonNull String hostname,
            @NonNull Integer port,
            @NonNull String password,
            @NonNull RustEventSourceStrategy eventSourceStrategy
    ) {
        this.hostname = Objects.requireNonNull(hostname);
        this.port = Objects.requireNonNull(port);
        this.password = Objects.requireNonNull(password);
        this.eventSourceStrategy = Objects.requireNonNull(eventSourceStrategy);
    }
}
