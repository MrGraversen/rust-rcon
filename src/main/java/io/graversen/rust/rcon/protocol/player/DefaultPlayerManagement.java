package io.graversen.rust.rcon.protocol.player;

import io.graversen.rust.rcon.RustPlayer;
import io.graversen.rust.rcon.RustRconResponse;
import io.graversen.rust.rcon.protocol.AdminCodec;
import io.graversen.rust.rcon.protocol.util.PlayerName;
import io.graversen.rust.rcon.protocol.util.SteamId64;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@Slf4j
@RequiredArgsConstructor
public class DefaultPlayerManagement implements PlayerManagement {
    private final @NonNull AdminCodec adminCodec;

    @Override
    public CompletableFuture<List<RustPlayer>> sleepingPlayers() {
        return adminCodec.sleepingPlayers().thenApply(mapSleepingPlayers());
    }

    Function<RustRconResponse, List<RustPlayer>> mapSleepingPlayers() {
        return rconResponse -> {
            try {
                final var message = rconResponse.getMessage();
                return Arrays.stream(message.split("\n"))
                        .filter(line -> !line.isBlank())
                        .filter(line -> !line.contains("sleeping users"))
                        .map(line -> line.split(":"))
                        .map(parts -> new RustPlayer(SteamId64.parseOrFail(parts[0].trim()), new PlayerName(parts[1].trim())))
                        .toList();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return List.of();
            }
        };
    }
}
