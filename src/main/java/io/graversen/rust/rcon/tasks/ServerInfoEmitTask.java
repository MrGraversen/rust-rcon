package io.graversen.rust.rcon.tasks;

import io.graversen.rust.rcon.RustServer;
import io.graversen.rust.rcon.event.server.ServerInfoEvent;
import io.graversen.rust.rcon.protocol.dto.ServerInfoDTO;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Slf4j
@RequiredArgsConstructor
public class ServerInfoEmitTask extends RconTask {
    private final @NonNull RustServer server;
    private final @NonNull Supplier<CompletableFuture<ServerInfoDTO>> serverInfoGetter;
    private final @NonNull Consumer<ServerInfoEvent> serverInfoEmitter;

    @Override
    public void execute() {
        serverInfoGetter.get()
                .thenApply(serverInfoEventMapper())
                .thenAccept(serverInfoEmitter);
    }

    Function<ServerInfoDTO, ServerInfoEvent> serverInfoEventMapper() {
        return serverInfo -> new ServerInfoEvent(server, serverInfo);
    }
}
