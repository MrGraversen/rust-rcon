package io.graversen.rust.rcon.protocol.broadcast;

import io.graversen.rust.rcon.RustRconResponse;
import io.graversen.rust.rcon.protocol.util.SteamId64;
import lombok.NonNull;

import java.util.concurrent.CompletableFuture;

public interface BroadcastManagement {
    CompletableFuture<RustRconResponse> broadcast(@NonNull String message, @NonNull SteamId64 speakerSteamId);

    CompletableFuture<RustRconResponse> whisper(@NonNull String message, @NonNull SteamId64 speakerSteamId, @NonNull SteamId64 targetSteamId);
}
