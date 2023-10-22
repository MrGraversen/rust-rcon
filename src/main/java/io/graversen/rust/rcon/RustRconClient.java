package io.graversen.rust.rcon;

import io.graversen.rust.rcon.util.EventEmitter;
import io.graversen.rust.rcon.util.JsonMapper;
import lombok.NonNull;

import javax.annotation.Nullable;
import java.io.Closeable;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public interface RustRconClient extends EventEmitter, Closeable {
    CompletableFuture<RustRconResponse> send(@NonNull RustRconRequest rustRconRequest);

    Function<RustRconRequest, RustRconRequestDTO> mapRequest();

    Function<RustRconResponseDTO, RustRconResponse> mapResponse(@Nullable RustRconRequest request);

    JsonMapper jsonMapper();

    void connect();

    RustServer rustServer();

    default String name() {
        return "rust-rcon";
    }

    default Integer initialMessageIdentifier() {
        return 1;
    }
}
