package io.graversen.rust.rcon;

import io.graversen.rust.rcon.protocol.RustRconMessage;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@Slf4j
public class NoOpRustRconRouter implements RustRconRouter {
    @Override
    public <T> CompletableFuture<T> send(@NonNull RustRconMessage rustRconMessage, @NonNull Function<RustRconResponse, T> mapper) {
        log.info("{}", rustRconMessage.get());
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<RustRconResponse> send(@NonNull RustRconMessage rustRconMessage) {
        log.info("{}", rustRconMessage.get());
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }
}
