package io.graversen.rust.rcon;

import io.graversen.rust.rcon.protocol.Codec;
import io.graversen.rust.rcon.protocol.dto.ServerInfoDTO;
import io.graversen.rust.rcon.tasks.RconTask;
import io.graversen.rust.rcon.util.EventEmitter;
import lombok.NonNull;

import javax.annotation.Nullable;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public interface RustRconService extends EventEmitter {
    Codec codec();

    void start();

    void stop();

    CompletableFuture<ServerInfoDTO> serverInfo();

    void schedule(@NonNull RconTask task, @NonNull Duration fixedDelay, @Nullable Duration initialDelay);
}
