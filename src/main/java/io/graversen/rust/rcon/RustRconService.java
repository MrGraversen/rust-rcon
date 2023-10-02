package io.graversen.rust.rcon;

import io.graversen.rust.rcon.protocol.Codec;
import io.graversen.rust.rcon.protocol.dto.ServerInfoDTO;
import io.graversen.rust.rcon.protocol.oxide.OxideManagement;
import io.graversen.rust.rcon.protocol.player.PlayerManagement;
import io.graversen.rust.rcon.tasks.RconTask;
import io.graversen.rust.rcon.util.EventEmitter;
import lombok.NonNull;

import javax.annotation.Nullable;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface RustRconService extends EventEmitter {
    Codec codec();

    void start();

    void stop();

    CompletableFuture<ServerInfoDTO> serverInfo();

    OxideManagement oxideManagement();

    PlayerManagement playerManagement();

    void schedule(@NonNull RconTask task, @NonNull Duration fixedDelay, @Nullable Duration initialDelay);

    Optional<RustDiagnostics> diagnostics();

    List<FullRustPlayer> players();

    List<RustTeam> teams();
}
