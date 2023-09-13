package io.graversen.rust.rcon;

import com.google.common.eventbus.Subscribe;
import io.graversen.rust.rcon.event.server.ServerInfoEvent;
import io.graversen.rust.rcon.util.CommonUtils;
import io.graversen.rust.rcon.util.RustUtils;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.function.Consumer;
import java.util.function.Function;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class ServerInfoDiagnosticsEventListener {
    private final @NonNull Consumer<RustDiagnostics> diagnosticsConsumer;

    @Subscribe
    public void onServerInfo(ServerInfoEvent serverInfoEvent) {
        final var rustDiagnostics = mapRustDiagnostics().apply(serverInfoEvent);
        diagnosticsConsumer.accept(rustDiagnostics);
    }

    Function<ServerInfoEvent, RustDiagnostics> mapRustDiagnostics() {
        return serverInfoEvent -> new SimpleRustRconDiagnostics(
                CommonUtils.now(),
                Duration.ofSeconds(serverInfoEvent.getServerInfo().getUptimeSeconds()),
                RustUtils.parseRustDateTime(serverInfoEvent.getServerInfo().getSaveCreatedTime()),
                String.valueOf(serverInfoEvent.getServerInfo().getVersion()),
                serverInfoEvent.getServerInfo().getProtocol(),
                serverInfoEvent.getServerInfo().getMaxPlayers(),
                serverInfoEvent.getServerInfo().getCurrentPlayers(),
                RustUtils.parseRustDateTime(serverInfoEvent.getServerInfo().getGameDateTime()),
                BigDecimal.valueOf(serverInfoEvent.getServerInfo().getFrameRate())
        );
    }
}
