package io.graversen.rust.rcon.protocol.broadcast;

import io.graversen.rust.rcon.RustRconResponse;
import io.graversen.rust.rcon.protocol.Codec;
import io.graversen.rust.rcon.protocol.util.SteamId64;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class DefaultBroadcastManagement implements BroadcastManagement {
    private final @NonNull Codec codec;

    @Override
    public CompletableFuture<RustRconResponse> broadcast(@NonNull String message, @NonNull SteamId64 speakerSteamId) {
        final var rconMessage = codec.raw("broadcast %s %s".formatted(quote(message), speakerSteamId.get()));
        return codec.send(rconMessage);
    }

    @Override
    public CompletableFuture<RustRconResponse> whisper(@NonNull String message, @NonNull SteamId64 speakerSteamId, @NonNull SteamId64 targetSteamId) {
        final var rconMessage = codec.raw("whisper %s %s %s".formatted(quote(message), speakerSteamId.get(), targetSteamId.get()));
        return codec.send(rconMessage);
    }

    private String quote(@NonNull String value) {
        return "\"%s\"".formatted(value
                .replace("\\", "\\\\")
                .replace("\"", "\\\""));
    }
}
