package io.graversen.rust.rcon.protocol;

import io.graversen.rust.rcon.RustRconResponse;
import io.graversen.rust.rcon.protocol.util.PlayerName;
import io.graversen.rust.rcon.protocol.util.SteamId64;
import lombok.NonNull;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public interface AdminCodec {
    CompletableFuture<RustRconResponse> kickPlayer(@NonNull SteamId64 steamId64, @Nullable String reason);

    CompletableFuture<RustRconResponse> kickAllPlayers();

    CompletableFuture<RustRconResponse> banPlayer(@NonNull SteamId64 steamId64, @Nullable PlayerName playerName, @Nullable String reason);

    CompletableFuture<RustRconResponse> unbanPlayer(@NonNull SteamId64 steamId64);

    CompletableFuture<RustRconResponse> addOwner(@NonNull SteamId64 steamId64, @Nullable PlayerName playerName);

    CompletableFuture<RustRconResponse> removeOwner(@NonNull SteamId64 steamId64);

    CompletableFuture<RustRconResponse> mutePlayer(@NonNull SteamId64 steamId64);

    CompletableFuture<RustRconResponse> unmutePlayer(@NonNull SteamId64 steamId64);

    CompletableFuture<RustRconResponse> serverInfo();

    CompletableFuture<RustRconResponse> playerList();

    CompletableFuture<RustRconResponse> sleepingPlayers();

    CompletableFuture<RustRconResponse> teamInfo(@NonNull SteamId64 steamId64);
}
