package io.graversen.rust.rcon;

import io.graversen.rust.rcon.protocol.Codec;
import io.graversen.rust.rcon.util.EventEmitter;

public interface RustRconService extends EventEmitter {
    Codec codec();

    void start();

    void stop();
}
