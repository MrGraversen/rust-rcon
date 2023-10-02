package io.graversen.rust.rcon;

import com.google.common.eventbus.Subscribe;
import io.graversen.rust.rcon.event.server.RustPlayersEvent;
import io.graversen.rust.rcon.protocol.dto.RustPlayerDTO;
import io.graversen.rust.rcon.protocol.util.PlayerName;
import io.graversen.rust.rcon.protocol.util.SteamId64;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class RustPlayerEventListener {
    private final @NonNull Consumer<List<FullRustPlayer>> rustPlayersConsumer;

    @Subscribe
    public void onRustPlayers(RustPlayersEvent rustPlayersEvent) {
        final var rustPlayers = mapRustPlayers().apply(rustPlayersEvent);
        rustPlayersConsumer.accept(rustPlayers);
    }

    Function<RustPlayersEvent, List<FullRustPlayer>> mapRustPlayers() {
        return rustPlayersEvent -> {
            final var rustPlayers = rustPlayersEvent.getRustPlayers();
            return rustPlayers.stream()
                    .map(mapRustPlayer())
                    .toList();
        };
    }

    Function<RustPlayerDTO, FullRustPlayer> mapRustPlayer() {
        return rustPlayerDTO -> new FullRustPlayer(
                SteamId64.parseOrFail(rustPlayerDTO.getSteamId()),
                new PlayerName(rustPlayerDTO.getPlayerName()),
                rustPlayerDTO.getPing(),
                rustPlayerDTO.getIpAddress().split(":")[0],
                Duration.ofSeconds(rustPlayerDTO.getConnectedSeconds()),
                BigDecimal.valueOf(rustPlayerDTO.getHealth())
        );
    }
}
