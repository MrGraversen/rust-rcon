package io.graversen.rust.rcon.event.umod;

import io.graversen.rust.rcon.protocol.oxide.OxidePlugin;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface UmodBridgeManagement {
    CompletableFuture<Optional<OxidePlugin>> rustRconBridgePlugin();

    CompletableFuture<Boolean> isRustRconBridgeInstalled();
}
