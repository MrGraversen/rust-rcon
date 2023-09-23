package io.graversen.rust.rcon.protocol.oxide;

import io.graversen.rust.rcon.RustRconResponse;
import lombok.NonNull;

import java.util.concurrent.CompletableFuture;

public interface OxideCodec {
    CompletableFuture<RustRconResponse> oxidePlugins();

    CompletableFuture<RustRconResponse> grant(@NonNull OxidePermission permission);

    CompletableFuture<RustRconResponse> revoke(@NonNull OxidePermission permission);
}
