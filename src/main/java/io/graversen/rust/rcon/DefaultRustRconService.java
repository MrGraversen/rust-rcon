package io.graversen.rust.rcon;

import com.google.common.eventbus.EventBus;
import io.graversen.rust.rcon.event.AutoConfiguringRustEventService;
import io.graversen.rust.rcon.event.RustEventService;
import io.graversen.rust.rcon.event.rcon.RconLogSubscriber;
import io.graversen.rust.rcon.protocol.Codec;
import io.graversen.rust.rcon.protocol.DefaultRustCodec;
import io.graversen.rust.rcon.util.EventEmitter;
import io.graversen.rust.rcon.util.Lazy;
import io.graversen.rust.rcon.ws.ReconnectingRustWebSocketClient;
import io.graversen.rust.rcon.ws.RustWebSocketClient;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@RequiredArgsConstructor
public class DefaultRustRconService implements RustRconService {
    private final AtomicBoolean isRconLogEnabled = new AtomicBoolean(false);

    private final @NonNull RustRconConfiguration configuration;

    private final Lazy<EventBus> eventBus = Lazy.of(this::createEventBus);
    private final Lazy<RustEventService> rustEventService = Lazy.of(this::delegatingCreateRustEventService);
    private final Lazy<RustWebSocketClient> rustWebSocketClient = Lazy.of(this::createWebSocketClient);
    private final Lazy<RustRconClient> rustRconClient = Lazy.of(this::createRustRconClient);
    private final Lazy<RustRconRouter> rustRconRouter = Lazy.of(this::createRustRconRouter);
    private final Lazy<Codec> codec = Lazy.of(this::createCodec);

    @Override
    public Codec codec() {
        return codec.get();
    }

    @Override
    public void start() {
        final var startedAt = LocalDateTime.now();
        log.debug("Starting {}", getClass().getSimpleName());
        rustEventService.get().configure();
        rustRconRouter.get().start();
        log.debug("Successfully started after {} ms", Duration.between(startedAt, LocalDateTime.now()).toMillis());
    }

    @Override
    public void stop() {
        final var startedAt = LocalDateTime.now();
        log.debug("Stopping {}", getClass().getSimpleName());
        rustRconRouter.get().stop();
        log.debug("Successfully stopped after {} ms", Duration.between(startedAt, LocalDateTime.now()).toMillis());
    }

    public void enableRconLogger() {
        if (isRconLogEnabled.compareAndSet(false, true)) {
            eventBus.get().register(new RconLogSubscriber((client, message) -> log.debug("[{}]: {}", client, message)));
        }
    }

    protected Codec createCodec() {
        return new DefaultRustCodec(
                rustRconRouter.get()
        );
    }

    protected RustWebSocketClient createWebSocketClient() {
        return new ReconnectingRustWebSocketClient(
                configuration.getHostname(),
                configuration.getPort(),
                configuration.getPassword(),
                eventBus.get()
        );
    }

    protected RustRconClient createRustRconClient() {
        return new DefaultRustRconClient(
                rustWebSocketClient.get(),
                eventBus.get()
        );
    }

    protected RustRconRouter createRustRconRouter() {
        return new DefaultRustRconRouter(
                rustRconClient.get(),
                eventBus.get()
        );
    }

    protected RustEventService createRustEventService(@NonNull EventBus eventBus) {
        return new AutoConfiguringRustEventService(eventBus);
    }

    protected EventBus createEventBus() {
        return new EventBus();
    }

    private RustEventService delegatingCreateRustEventService() {
        final var resolvedEventBus = eventBus.get();
        return createRustEventService(resolvedEventBus);
    }

    @Override
    public void registerEvents(@NonNull Object subscriber) {
        eventBus.get().register(subscriber);
    }
}
