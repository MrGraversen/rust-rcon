package io.graversen.rust.rcon;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import io.graversen.rust.rcon.event.rcon.RconReceivedEvent;
import io.graversen.rust.rcon.event.ws.WsClosedEvent;
import io.graversen.rust.rcon.event.ws.WsMessageEvent;
import io.graversen.rust.rcon.event.ws.WsOpenedEvent;
import io.graversen.rust.rcon.util.DefaultJsonMapper;
import io.graversen.rust.rcon.util.JsonMapper;
import io.graversen.rust.rcon.util.Lazy;
import io.graversen.rust.rcon.ws.RustWebSocketClient;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.framing.CloseFrame;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

@Slf4j
@RequiredArgsConstructor
public class DefaultRustRconClient implements RustRconClient {
    private final Object lock = new Object();
    private final Lazy<RustServer> rustServer = Lazy.of(this::getRustServer);
    private final AtomicBoolean isInitialized = new AtomicBoolean(false);

    private final @NonNull RustWebSocketClient webSocketClient;
    private final @NonNull EventBus eventBus;

    private Cache<Integer, CompletableFuture<RustRconResponse>> asyncResponses;
    private Cache<Integer, RustRconRequest> asyncRequests;
    private JsonMapper jsonMapper;

    @Override
    public CompletableFuture<RustRconResponse> send(@NonNull RustRconRequest request) {
        if (webSocketClient.isOpen()) {
            final var mappedRequest = mapRequest().apply(request);
            final var json = jsonMapper().toJson(mappedRequest);

            final var responseFuture = new CompletableFuture<RustRconResponse>();
            getAsyncRequests().put(request.getIdentifier(), request);
            getAsyncResponses().put(request.getIdentifier(), responseFuture);
            webSocketClient.send(json);

            return responseFuture;
        } else {
            return CompletableFuture.failedFuture(
                    new IllegalStateException(String.format("%s instance is not open", webSocketClient.getClass().getSimpleName()))
            );
        }
    }

    @Override
    public Function<RustRconRequest, RustRconRequestDTO> mapRequest() {
        return request -> new RustRconRequestDTO(
                request.getIdentifier(),
                request.getMessage(),
                name()
        );
    }

    @Override
    public Function<RustRconResponseDTO, RustRconResponse> mapResponse(@Nullable RustRconRequest request) {
        return rustRconResponseDTO -> new RustRconResponse(
                Objects.requireNonNullElse(rustRconResponseDTO.getIdentifier(), -1),
                rustRconResponseDTO.getMessage(),
                rustRconResponseDTO.getType(),
                rustServer.get(),
                request
        );
    }

    @Override
    public JsonMapper jsonMapper() {
        if (jsonMapper == null) {
            jsonMapper = new DefaultJsonMapper();
        }

        return jsonMapper;
    }

    @Override
    public void connect() {
        log.debug("Connecting {}", getClass().getSimpleName());
        webSocketClient.registerEvents(this);
        webSocketClient.connect();
    }

    @Override
    public RustServer rustServer() {
        return getRustServer();
    }

    @Override
    @SneakyThrows
    public void close() throws IOException {
        if (isInitialized.get()) {
            webSocketClient.closeBlocking();
        } else {
            log.debug("Waiting for {} to initialize", getClass().getSimpleName());
            synchronized (lock) {
                while (!isInitialized.get()) {
                    lock.wait();
                }
                webSocketClient.closeConnection(CloseFrame.NORMAL, String.format("Close requested by %s", getClass().getSimpleName()));
            }
        }
    }

    @Subscribe
    public void onWsClosedEvent(@NonNull WsClosedEvent wsClosedEvent) {
        log.debug("Closed {}: {} ({})", getClass().getSimpleName(), wsClosedEvent.getReason(), wsClosedEvent.getCode());
    }

    @Subscribe
    public void onWsOpenedEvent(@NonNull WsOpenedEvent wsOpenedEvent) {
        synchronized (lock) {
            log.debug("Connected {}: {}", getClass().getSimpleName(), wsOpenedEvent.getServerUri());
            isInitialized.set(true);
            lock.notifyAll();
        }
    }

    @Subscribe
    public void onWsMessage(@NonNull WsMessageEvent messageEvent) {
        final var rconMessage = jsonMapper().fromJson(messageEvent.getMessage(), RustRconResponseDTO.class);
        final var rconRequest = getAsyncRequests().getIfPresent(rconMessage.getIdentifier());

        final var mappedRconMessage = mapResponse(rconRequest).apply(rconMessage);

        if (rconMessage.getIdentifier() >= initialMessageIdentifier()) {
            final var responseFutureOrNull = getAsyncResponses().getIfPresent(rconMessage.getIdentifier());

            if (responseFutureOrNull != null) {
                responseFutureOrNull.complete(mappedRconMessage);
            } else {
                log.warn("Could not resolve response future for ID: {} (Discarding it)", rconMessage.getIdentifier());
            }
        }

        eventBus.post(new RconReceivedEvent(name(), mappedRconMessage));
    }

    @Synchronized
    private Cache<Integer, CompletableFuture<RustRconResponse>> getAsyncResponses() {
        if (asyncResponses == null) {
            asyncResponses = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.MINUTES).build();
        }

        return asyncResponses;
    }

    @Synchronized
    private Cache<Integer, RustRconRequest> getAsyncRequests() {
        if (asyncRequests == null) {
            asyncRequests = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.MINUTES).build();
        }

        return asyncRequests;
    }

    @Override
    public void registerEvents(@NonNull Object subscriber) {
        eventBus.register(subscriber);
    }

    @Override
    public String toString() {
        return String.format("%s[%s]", getClass().getSimpleName(), getRustServer().getServerUri());
    }

    private RustServer getRustServer() {
        return new SimpleRustServer(name(), webSocketClient.getServerUri());
    }
}
