package io.graversen.rust.rcon.protocol;

import io.graversen.rust.rcon.RustRconResponse;
import io.graversen.rust.rcon.protocol.util.SteamId64;
import lombok.NonNull;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public interface EventCodec {
    CompletableFuture<RustRconResponse> callAirDrop();

    CompletableFuture<RustRconResponse> callPatrolHelicopter();

    CompletableFuture<RustRconResponse> strafePatrolHelicopter(@NonNull SteamId64 steamId64);

    CompletableFuture<RustRconResponse> patrolHelicopterLifetime(@NonNull Duration lifetime);
}
