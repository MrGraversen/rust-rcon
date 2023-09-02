package io.graversen.rust.rcon.util;

import lombok.NonNull;

public interface EventEmitter {
    void registerEvents(@NonNull Object subscriber);
}
