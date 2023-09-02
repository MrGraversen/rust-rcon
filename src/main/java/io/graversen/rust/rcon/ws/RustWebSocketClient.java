package io.graversen.rust.rcon.ws;

import com.google.common.eventbus.EventBus;
import io.graversen.rust.rcon.event.ws.WsClosedEvent;
import io.graversen.rust.rcon.event.ws.WsErrorEvent;
import io.graversen.rust.rcon.event.ws.WsMessageEvent;
import io.graversen.rust.rcon.event.ws.WsOpenedEvent;
import io.graversen.rust.rcon.util.CommonUtils;
import io.graversen.rust.rcon.util.EventEmitter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringSubstitutor;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.Closeable;
import java.net.URI;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

@Slf4j
public class RustWebSocketClient extends WebSocketClient implements Closeable, EventEmitter {
    private static final String LOG_TEMPLATE = "[${serverUri}]: ${message}";
    private static final String SERVER_URI_TEMPLATE = "ws://${hostname}:${port}/${password}";
    private static final String SERVER_URI_TEMPLATE_REDACTED = "ws://${hostname}:${port}/*";

    private final EventBus eventBus;
    private final String serverUriRedacted;
    private final AtomicBoolean isOpen;

    public RustWebSocketClient(
            @NonNull String hostname,
            @NonNull Integer port,
            @NonNull String password,
            @NonNull EventBus eventBus
    ) {
        this(serverUri(hostname, port, password), eventBus);
    }

    public RustWebSocketClient(@NonNull URI serverUri, @NonNull EventBus eventBus) {
        super(serverUri);
        this.eventBus = eventBus;
        this.serverUriRedacted = StringSubstitutor.replace(
                SERVER_URI_TEMPLATE_REDACTED,
                Map.of(
                        "hostname", serverUri.getHost(),
                        "port", serverUri.getPort()
                )
        );
        this.isOpen = new AtomicBoolean(false);
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        isOpen.set(true);
        setConnectionLostTimeout(connectionLostTimeout().toSecondsPart());
        eventBus.post(new WsOpenedEvent(serverUriRedacted));
    }

    @Override
    public void onMessage(String message) {
        eventBus.post(new WsMessageEvent(serverUriRedacted, message));
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        isOpen.set(false);
        eventBus.post(new WsClosedEvent(serverUriRedacted, code, reason));
    }

    @Override
    public void onError(Exception ex) {
        log.error(ex.getMessage(), ex);
        eventBus.post(new WsErrorEvent(serverUriRedacted, ex));
    }

    @Override
    public void registerEvents(@NonNull Object subscriber) {
        eventBus.register(subscriber);
    }

    public boolean isOpen() {
        return isOpen.get();
    }

    public String getServerUri() {
        return serverUriRedacted;
    }

    @Override
    public String toString() {
        return String.format("%s[%s]", getClass().getSimpleName(), getServerUri());
    }

    protected void log(@NonNull String message, Object... args) {
        log(log::info, message, args);
    }

    protected Duration connectionLostTimeout() {
        return Duration.ZERO;
    }

    protected void log(@NonNull Consumer<String> logger, @NonNull String message, Object... args) {
        message = String.format(message, args);
        message = StringSubstitutor.replace(
                LOG_TEMPLATE,
                Map.of(
                        "serverUri", serverUriRedacted,
                        "message", message
                )
        );
        logger.accept(message);
    }

    private static URI serverUri(
            @NonNull String hostname,
            @NonNull Integer port,
            @NonNull String password
    ) {
        final var compiledServerUri = StringSubstitutor.replace(
                SERVER_URI_TEMPLATE,
                Map.of(
                        "hostname", hostname,
                        "port", port,
                        "password", password
                )
        );
        return CommonUtils.uri(compiledServerUri);
    }
}
