package io.graversen.rust.rcon;

import com.google.common.eventbus.EventBus;
import io.graversen.rust.rcon.event.rcon.RconProtocolExchangeEvent;
import io.graversen.rust.rcon.protocol.RustRconMessage;
import io.graversen.rust.rcon.util.CommonExecutor;
import io.graversen.rust.rcon.util.EventEmitter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Slf4j
@RequiredArgsConstructor
public class DefaultRustRconRouter implements RustRconRouter, EventEmitter {
    private final RustRconClient rconClient;
    private final EventBus eventBus;
    private Supplier<Integer> identifierGenerator;

    protected Supplier<Integer> identifierGenerator() {
        final var atomicInteger = new AtomicInteger(rconClient.initialMessageIdentifier());
        return atomicInteger::getAndIncrement;
    }

    @Override
    public void start() {
        log.debug("Starting {}", getClass().getSimpleName());
        rconClient.connect();
    }

    @Override
    public <T> CompletableFuture<T> send(@NonNull RustRconMessage rustRconMessage, @NonNull Function<RustRconResponse, T> mapper) {
        return send(rustRconMessage).thenApply(mapper);
    }

    @Override
    public CompletableFuture<RustRconResponse> send(@NonNull RustRconMessage rustRconMessage) {
        final var identifier = getIdentifierGenerator().get();
        final var message = rustRconMessage.get();
        final var rconRequest = new RustRconRequest(identifier, message);

        return rconClient.send(rconRequest).whenCompleteAsync(handleRconProtocolExchange(rconRequest), CommonExecutor.getInstance());
    }

    @Override
    @SneakyThrows
    public void stop() {
        rconClient.close();
    }

    @Synchronized
    private Supplier<Integer> getIdentifierGenerator() {
        if (identifierGenerator == null) {
            identifierGenerator = identifierGenerator();
        }

        return identifierGenerator;
    }

    @Override
    public void registerEvents(@NonNull Object subscriber) {
        eventBus.register(subscriber);
    }

    private BiConsumer<RustRconResponse, Throwable> handleRconProtocolExchange(@NonNull RustRconRequest request) {
        return (response, throwable) -> {
            if (throwable != null) {
                log.error(throwable.getMessage(), throwable);
            } else if (response != null) {
                eventBus.post(new RconProtocolExchangeEvent(request, response));
            } else {
                log.error("Unable to determine outcome of RCON protocol exchange");
            }
        };
    }
}
