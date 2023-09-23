package io.graversen.rust.rcon.protocol;

import io.graversen.rust.rcon.RustRconResponse;
import io.graversen.rust.rcon.protocol.oxide.OxideCodec;
import lombok.NonNull;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public interface Codec {
    AdminCodec admin();

    SettingsCodec settings();

    EventCodec event();

    OxideCodec oxide();

    CompletableFuture<RustRconResponse> send(@NonNull RustRconMessage rustRconMessage);

    <T> CompletableFuture<T> send(@NonNull RustRconMessage rustRconMessage, @NonNull Function<RustRconResponse, T> mapper);

    default RustRconMessage raw(@NonNull String template) {
        return raw(template, Map.of());
    }

    RustRconMessage raw(@NonNull String template, @NonNull Map<String, String> values);
}
