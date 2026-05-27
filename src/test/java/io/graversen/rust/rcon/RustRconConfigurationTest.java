package io.graversen.rust.rcon;

import io.graversen.rust.rcon.event.RustEventSourceStrategy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RustRconConfigurationTest {
    @Test
    void defaultsEventSourceStrategyToRcon() {
        final var configuration = new RustRconConfiguration("localhost", 28016, "password");

        assertEquals(RustEventSourceStrategy.RCON, configuration.getEventSourceStrategy());
    }

    @Test
    void supportsExplicitUmodEventSourceStrategy() {
        final var configuration = new RustRconConfiguration(
                "localhost",
                28016,
                "password",
                RustEventSourceStrategy.UMOD
        );

        assertEquals(RustEventSourceStrategy.UMOD, configuration.getEventSourceStrategy());
    }
}
