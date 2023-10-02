package io.graversen.rust.rcon.tasks;

import io.graversen.rust.rcon.RustServer;
import io.graversen.rust.rcon.RustTeam;
import io.graversen.rust.rcon.event.server.RustTeamsEvent;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Slf4j
@RequiredArgsConstructor
public class RustTeamsEmitTask extends RconTask {
    private final @NonNull RustServer server;
    private final @NonNull Supplier<CompletableFuture<List<RustTeam>>> rustTeamsGetter;
    private final @NonNull Consumer<RustTeamsEvent> rustTeamsEventEmitter;

    @Override
    public void execute() {
        rustTeamsGetter.get()
                .thenApply(rustTeamsEventMapper())
                .thenAccept(rustTeamsEventEmitter);
    }

    Function<List<RustTeam>, RustTeamsEvent> rustTeamsEventMapper() {
        return rustTeams -> new RustTeamsEvent(server, rustTeams);
    }
}
