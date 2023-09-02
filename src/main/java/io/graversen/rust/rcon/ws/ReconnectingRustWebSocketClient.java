package io.graversen.rust.rcon.ws;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import io.graversen.rust.rcon.event.ws.WsClosedEvent;
import io.graversen.rust.rcon.tasks.ReconnectRconTask;
import io.graversen.rust.rcon.util.CommonExecutor;
import io.graversen.rust.rcon.util.CommonUtils;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.framing.CloseFrame;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ReconnectingRustWebSocketClient extends RustWebSocketClient {
    private static final Duration DEFAULT_RECONNECT_DELAY = Duration.ofSeconds(1);

    private final ScheduledExecutorService reconnectService;

    public ReconnectingRustWebSocketClient(@NonNull String hostname, @NonNull Integer port, @NonNull String password, @NonNull EventBus eventBus) {
        super(hostname, port, password, eventBus);
        this.reconnectService = CommonExecutor.getInstance();
    }

    @Override
    public void connect() {
        log.debug("Connecting {}", getClass().getSimpleName());
        registerEvents(this);
        super.connect();
    }

    @Subscribe
    public void onWsClosedEvent(@NonNull WsClosedEvent wsClosedEvent) {
        if (wsClosedEvent.getCode() != CloseFrame.NORMAL) {
            final var reconnectDelay = Objects.requireNonNullElse(reconnectDelay(), DEFAULT_RECONNECT_DELAY);
            log(
                    log::info,
                    "Unexpected close: %s (Code: %d) - Attempting to reconnect after %d ms",
                    CommonUtils.isNullOrEmpty(wsClosedEvent.getReason()) ? "(none)" : wsClosedEvent.getReason(),
                    wsClosedEvent.getCode(),
                    reconnectDelay.toMillis()
            );
            reconnectService.schedule(new ReconnectRconTask(this::reconnect), reconnectDelay.toMillis(), TimeUnit.MILLISECONDS);
        }
    }

    protected Duration reconnectDelay() {
        return DEFAULT_RECONNECT_DELAY;
    }
}
