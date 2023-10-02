package io.graversen.rust.rcon;

import com.google.common.eventbus.EventBus;
import io.graversen.rust.rcon.event.AutoConfiguringRustEventService;
import io.graversen.rust.rcon.event.RustEventService;
import io.graversen.rust.rcon.event.rcon.RconLogSubscriber;
import io.graversen.rust.rcon.protocol.Codec;
import io.graversen.rust.rcon.protocol.DefaultRustCodec;
import io.graversen.rust.rcon.protocol.dto.RustDtoMappers;
import io.graversen.rust.rcon.protocol.dto.ServerInfoDTO;
import io.graversen.rust.rcon.protocol.oxide.DefaultOxideManagement;
import io.graversen.rust.rcon.protocol.oxide.OxideManagement;
import io.graversen.rust.rcon.protocol.player.DefaultPlayerManagement;
import io.graversen.rust.rcon.protocol.player.PlayerManagement;
import io.graversen.rust.rcon.tasks.RconTask;
import io.graversen.rust.rcon.tasks.RustPlayersEmitTask;
import io.graversen.rust.rcon.tasks.RustTeamsEmitTask;
import io.graversen.rust.rcon.tasks.ServerInfoEmitTask;
import io.graversen.rust.rcon.util.DefaultJsonMapper;
import io.graversen.rust.rcon.util.Lazy;
import io.graversen.rust.rcon.ws.ReconnectingRustWebSocketClient;
import io.graversen.rust.rcon.ws.RustWebSocketClient;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@RequiredArgsConstructor
public class DefaultRustRconService implements RustRconService {
    private final AtomicBoolean isRconLogEnabled = new AtomicBoolean(false);
    private final AtomicReference<RustDiagnostics> diagnostics = new AtomicReference<>(null);
    private final AtomicReference<List<FullRustPlayer>> rustPlayers = new AtomicReference<>(List.of());
    private final AtomicReference<List<RustTeam>> rustTeams = new AtomicReference<>(List.of());

    private final @NonNull RustRconConfiguration configuration;

    private final Lazy<ScheduledExecutorService> scheduledExecutorService = Lazy.of(this::createScheduledExecutorService);
    private final Lazy<RustDtoMappers> rustDtoMappers = Lazy.of(this::createRustDtoMappers);
    private final Lazy<EventBus> eventBus = Lazy.of(this::createEventBus);
    private final Lazy<RustEventService> rustEventService = Lazy.of(this::delegatingCreateRustEventService);
    private final Lazy<RustWebSocketClient> rustWebSocketClient = Lazy.of(this::createWebSocketClient);
    private final Lazy<RustRconClient> rustRconClient = Lazy.of(this::createRustRconClient);
    private final Lazy<RustRconRouter> rustRconRouter = Lazy.of(this::createRustRconRouter);
    private final Lazy<Codec> codec = Lazy.of(this::createCodec);
    private final Lazy<OxideManagement> oxideManagement = Lazy.of(this::createOxideManagement);
    private final Lazy<PlayerManagement> playerManagement = Lazy.of(this::createPlayerManagement);

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
        runConfigure();
        log.debug("Successfully started after {} ms", Duration.between(startedAt, LocalDateTime.now()).toMillis());
    }

    @Override
    public void stop() {
        final var startedAt = LocalDateTime.now();
        log.debug("Stopping {}", getClass().getSimpleName());
        rustRconRouter.get().stop();
        log.debug("Successfully stopped after {} ms", Duration.between(startedAt, LocalDateTime.now()).toMillis());
    }

    @Override
    public CompletableFuture<ServerInfoDTO> serverInfo() {
        return codec().admin().serverInfo().thenApply(rustDtoMappers.get().mapServerInfo());
    }

    @Override
    public OxideManagement oxideManagement() {
        return oxideManagement.get();
    }

    @Override
    public PlayerManagement playerManagement() {
        return playerManagement.get();
    }

    @Override
    public void schedule(@NonNull RconTask task, @NonNull Duration fixedDelay, @Nullable Duration initialDelay) {
        final var wrappedTask = wrapRconTask(task);
        scheduledExecutorService.get().scheduleWithFixedDelay(
                wrappedTask,
                Objects.requireNonNullElse(initialDelay, Duration.ZERO).toMillis(),
                fixedDelay.toMillis(),
                TimeUnit.MILLISECONDS
        );
    }

    @Override
    public Optional<RustDiagnostics> diagnostics() {
        return Optional.ofNullable(diagnostics.get());
    }

    @Override
    public List<FullRustPlayer> players() {
        return List.copyOf(rustPlayers.get());
    }

    @Override
    public List<RustTeam> teams() {
        return List.copyOf(rustTeams.get());
    }

    @Override
    public void registerEvents(@NonNull Object subscriber) {
        eventBus.get().register(subscriber);
    }

    public void enableRconLogger() {
        if (isRconLogEnabled.compareAndSet(false, true)) {
            eventBus.get().register(new RconLogSubscriber((client, message) -> log.debug("[{}]: {}", client, message)));
        }
    }

    protected void configure() {
        final var serverInfoEmitTask = new ServerInfoEmitTask(
                rustRconClient.get().rustServer(),
                this::serverInfo,
                eventBus.get()::post
        );
        registerInternalTask(serverInfoEmitTask, Duration.ofSeconds(5));
        final var rustPlayersEmitTask = new RustPlayersEmitTask(
                rustRconClient.get().rustServer(),
                () -> codec().admin().playerList().thenApply(rustDtoMappers.get().mapRustPlayers()),
                eventBus.get()::post
        );
        registerInternalTask(rustPlayersEmitTask, Duration.ofSeconds(3));
        final var rustTeamsEmitTask = new RustTeamsEmitTask(
                rustRconClient.get().rustServer(),
                () -> playerManagement().getTeams(),
                eventBus.get()::post
        );
        registerInternalTask(rustTeamsEmitTask, Duration.ofMinutes(5), Duration.ofSeconds(10));
        registerRustDiagnosticsListener();
        registerRustPlayerEventListener();
        registerRustTeamsEventListener();
    }

    protected ScheduledExecutorService createScheduledExecutorService() {
        return Executors.newSingleThreadScheduledExecutor();
    }

    protected RustDtoMappers createRustDtoMappers() {
        return new RustDtoMappers(new DefaultJsonMapper());
    }

    protected Codec createCodec() {
        return new DefaultRustCodec(
                rustRconRouter.get()
        );
    }

    protected OxideManagement createOxideManagement() {
        return new DefaultOxideManagement(codec().oxide());
    }

    protected PlayerManagement createPlayerManagement() {
        return new DefaultPlayerManagement(codec().admin(), rustDtoMappers.get());
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

    protected Duration defaultTaskInitialDelay() {
        return Duration.ofSeconds(5);
    }

    private RustEventService delegatingCreateRustEventService() {
        final var resolvedEventBus = eventBus.get();
        return createRustEventService(resolvedEventBus);
    }

    private Runnable wrapRconTask(@NonNull RconTask rconTask) {
        return () -> {
            try {
                rconTask.run();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        };
    }

    private void registerInternalTask(@NonNull RconTask task, @NonNull Duration fixedDelay) {
        registerInternalTask(task, fixedDelay, null);
    }

    private void registerInternalTask(@NonNull RconTask task, @NonNull Duration fixedDelay, @Nullable Duration initialDelay) {
        log.info("Registering task: {} (Every {} seconds)", task.getClass().getSimpleName(), fixedDelay.getSeconds());
        initialDelay = Objects.requireNonNullElse(initialDelay, defaultTaskInitialDelay());
        schedule(task, fixedDelay, initialDelay);
    }

    private void registerRustDiagnosticsListener() {
        log.info("Registering {}", ServerInfoDiagnosticsEventListener.class.getSimpleName());
        registerEvents(new ServerInfoDiagnosticsEventListener(diagnostics::set));
    }

    private void registerRustPlayerEventListener() {
        log.info("Registering {}", RustPlayerEventListener.class.getSimpleName());
        registerEvents(new RustPlayerEventListener(rustPlayers::set));
    }

    private void registerRustTeamsEventListener() {
        log.info("Registering {}", RustTeamsEventListener.class.getSimpleName());
        registerEvents(new RustTeamsEventListener(rustTeams::set));
    }

    private void runConfigure() {
        log.info("Running internal configuration hook");
        configure();
    }
}
