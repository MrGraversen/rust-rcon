package io.graversen.rust.rcon;

import io.graversen.rust.rcon.protocol.Codec;

public interface RustRconService {
    Codec codec();

    void start();

    void stop();
}
