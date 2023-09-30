package io.graversen.rust.rcon.protocol.player;

import io.graversen.rust.rcon.RustPlayer;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface PlayerManagement {
    CompletableFuture<List<RustPlayer>> sleepingPlayers();
}
