package io.graversen.rust.rcon.protocol.player;

import io.graversen.rust.rcon.FullRustPlayer;
import io.graversen.rust.rcon.RustPlayer;
import io.graversen.rust.rcon.RustTeam;
import io.graversen.rust.rcon.protocol.util.SteamId64;
import lombok.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface PlayerManagement {
    CompletableFuture<List<FullRustPlayer>> players();

    CompletableFuture<List<RustPlayer>> sleepingPlayers();

    CompletableFuture<Optional<RustTeam>> team(@NonNull SteamId64 steamId64);

    CompletableFuture<List<RustTeam>> getTeams();
}
