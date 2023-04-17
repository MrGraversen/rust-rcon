package io.graversen.v1.rust.rcon.rustclient;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.graversen.fiber.event.bus.DefaultEventBus;
import io.graversen.fiber.event.bus.IEventBus;
import io.graversen.fiber.event.listeners.IEventListener;
import io.graversen.v1.rust.rcon.Constants;
import io.graversen.v1.rust.rcon.RconException;
import io.graversen.v1.rust.rcon.RconMessageTypes;
import io.graversen.v1.rust.rcon.events.IEventParser;
import io.graversen.v1.rust.rcon.events.RustEvents;
import io.graversen.v1.rust.rcon.events.parsers.DefaultRconMessageParser;
import io.graversen.v1.rust.rcon.events.parsers.IRconMessageParser;
import io.graversen.v1.rust.rcon.events.types.BaseRustEvent;
import io.graversen.v1.rust.rcon.events.types.server.*;
import io.graversen.v1.rust.rcon.logging.DefaultLogger;
import io.graversen.v1.rust.rcon.logging.ILogger;
import io.graversen.v1.rust.rcon.logging.LogLevels;
import io.graversen.v1.rust.rcon.objects.RconReceive;
import io.graversen.v1.rust.rcon.objects.RconRequest;
import io.graversen.v1.rust.rcon.polling.IPlayerPollingListener;
import io.graversen.v1.rust.rcon.protocol.Rcon;
import io.graversen.v1.rust.rcon.serialization.DefaultSerializer;
import io.graversen.v1.rust.rcon.websocket.DefaultWebSocketClient;
import io.graversen.v1.rust.rcon.websocket.IWebSocketClient;
import io.graversen.v1.rust.rcon.websocket.IWebSocketListener;
import io.graversen.trunk.io.serialization.interfaces.ISerializer;
import io.graversen.v1.rust.rcon.events.types.server.*;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class RustClient implements IRconClient, AutoCloseable
{
    private static final int DEFAULT_PORT = 25575;
    private static final Class[] DEFAULT_EVENT_CLASSES = new Class[]{RconClosedEvent.class, RconErrorEvent.class, RconOpenEvent.class};

    private final ILogger logger;
    private final ISerializer serializer;
    private final IWebSocketClient webSocketClient;
    private final IWebSocketListener webSocketListener;
    private final IEventBus eventBus;
    private final IRconMessageParser rconMessageParser;
    private final boolean registerDebugListeners;
    private final ConcurrentMap<RconMessageTypes, IEventParser<?>> eventParsers;

    private final AtomicInteger currentRequestCounter;
    private final Cache<Integer, CompletableFuture<RconReceive>> asyncRequests;
    private final ScheduledExecutorService pollers;

    private boolean open;
    private boolean defaultEventsRegistered;
    private boolean loggingEnabled;

    private boolean closing = false;

    private Rcon rcon;

    private RustClient(
            ILogger logger,
            ISerializer serializer,
            IWebSocketClient webSocketClient,
            IWebSocketListener webSocketListener,
            IEventBus eventBus,
            IRconMessageParser rconMessageParser,
            boolean registerDebugListeners)
    {
        this.logger = logger;
        this.serializer = serializer;
        this.webSocketClient = webSocketClient;
        this.webSocketListener = webSocketListener;
        this.eventBus = eventBus;
        this.rconMessageParser = rconMessageParser;
        this.registerDebugListeners = registerDebugListeners;
        this.eventParsers = new ConcurrentHashMap<>();

        this.open = false;
        this.defaultEventsRegistered = false;
        this.loggingEnabled = true;
        this.currentRequestCounter = new AtomicInteger(0);
        this.asyncRequests = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.MINUTES).build();
        this.pollers = Executors.newSingleThreadScheduledExecutor();
    }

    public Rcon rcon()
    {
        if (Objects.isNull(rcon))
        {
            rcon = new Rcon(this, getSerializer());
        }

        return rcon;
    }

    @Override
    public void send(String rconMessage)
    {
        final int identifier = currentRequestCounter.getAndIncrement();
        doSend(rconMessage, identifier);
    }

    @Override
    public CompletableFuture<RconReceive> sendAsync(String rconMessage)
    {
        final int identifier = currentRequestCounter.getAndIncrement();
        final CompletableFuture<RconReceive> completableFuture = new CompletableFuture<>();

        asyncRequests.put(identifier, completableFuture);
        doSend(rconMessage, identifier);

        return completableFuture;
    }

    @Override
    public RconReceive sendAsyncBlocking(String rconMessage)
    {
        return sendAsyncBlocking(rconMessage, 3, TimeUnit.SECONDS);
    }

    @Override
    public RconReceive sendAsyncBlocking(String rconMessage, long timeout, TimeUnit timeUnit)
    {
        try
        {
            return sendAsync(rconMessage).get(timeout, timeUnit);
        }
        catch (InterruptedException | ExecutionException | TimeoutException e)
        {
            return null;
        }
    }

    private void doSend(String rconMessage, int identifier)
    {
        if (!open)
        {
            throw new RconException("Cannot use RconClient before it has been opened");
        }

        final RconRequest rconRequest = new RconRequest(identifier, rconMessage, Constants.projectName());
        final String serializedPayload = getSerializer().serialize(rconRequest);

        if (registerDebugListeners)
        {
            getLogger().debug("Sending: %s", serializedPayload);
        }

        getWebSocketClient().send(serializedPayload);
    }

    public boolean open()
    {
        getLogger().info("Attempting to open RCON: %s", getWebSocketClient().connectionUriMasked());
        getEventBus().unregisterAllEventListeners();

        if (open)
        {
            throw new RconException("RustClient has already been opened");
        }

        if (getWebSocketListener() instanceof InternalWebSocketListener)
        {
            ((InternalWebSocketListener) getWebSocketListener()).setRustClient(this);
        }

        if (!defaultEventsRegistered)
        {
            if (!getEventBus().hasEventListener(RconMessageEvent.class))
            {
                getEventBus().registerEventListener(RconMessageEvent.class, this::asyncRequestListener);
            }

            if (!getEventBus().hasEventListener(RconErrorEvent.class))
            {
                getEventBus().registerEventListener(RconErrorEvent.class, this::rconErrorListener);
            }

            if (!getEventBus().hasEventListener(RconClosedEvent.class))
            {
                getEventBus().registerEventListener(RconClosedEvent.class, this::rconClosedListener);
            }
        }

        if (registerDebugListeners && !defaultEventsRegistered)
        {
            Arrays.stream(DEFAULT_EVENT_CLASSES).forEach(eventClass ->
                    {
                        if (!getEventBus().hasEventListener(eventClass))
                        {
                            getEventBus().registerEventListener(eventClass, event -> getLogger().debug(event.getClass().getSimpleName()));
                        }
                    }
            );
        }

        defaultEventsRegistered = true;

        getLogger().info("Starting EventBus instance...");

        getEventBus().start();

        getLogger().info("Connecting to remote socket...");
        final boolean wsOpen = getWebSocketClient().open();

        getLogger().info(wsOpen ? "Successfully opened RustClient!" : "Could not open RustClient!");

        this.open = wsOpen;
        return this.open;
    }

    public boolean isOpen()
    {
        return open;
    }

    public <T extends BaseServerEvent> void addServerEventListener(Class<T> eventClass, IEventListener<T> eventListener)
    {
        getEventBus().registerEventListener(eventClass, eventListener);
    }

    public <T extends BaseRustEvent> void addEventHandling(RustEvents rustEvent, IEventParser<T> eventParser, IEventListener<T> eventListener)
    {
        eventParsers.put(rustEvent.rconMessageType(), eventParser);
        getEventBus().registerEventListener(rustEvent.eventClass(), eventListener);
    }

    public <T extends BaseRustEvent> Optional<IEventParser<T>> getEventParser(RconMessageTypes rconMessage)
    {
        final IEventParser<T> eventParserOrNull = (IEventParser<T>) eventParsers.getOrDefault(rconMessage, null);
        return Optional.ofNullable(eventParserOrNull);
    }

    public ILogger getLogger()
    {
        return (loggingEnabled) ? logger : Constants.noOpLogger();
    }

    public ISerializer getSerializer()
    {
        return serializer;
    }

    public IWebSocketClient getWebSocketClient()
    {
        return webSocketClient;
    }

    public IWebSocketListener getWebSocketListener()
    {
        return webSocketListener;
    }

    public IEventBus getEventBus()
    {
        return eventBus;
    }

    public IRconMessageParser getRconMessageParser()
    {
        return rconMessageParser;
    }

    public void setLoggingEnabled(boolean loggingEnabled)
    {
        if (!loggingEnabled) getLogger().warning("Logging has been disabled");
        this.loggingEnabled = loggingEnabled;
    }

    @Override
    public synchronized void close()
    {
        if (!closing)
        {
            closing = true;
            getLogger().info("Closing RustClient");

            try
            {
                if (open)
                {
                    getWebSocketClient().close();
                }

                getEventBus().stop(false);
                pollers.shutdownNow();
                open = false;
            }
            catch (Exception e)
            {
                // ¯\_(ツ)_/¯
            }
        }
    }

    public void addPlayerPoller(IPlayerPollingListener playerPollingListener, long repeatInterval, TimeUnit timeUnit)
    {
        if (!open)
        {
            throw new RconException("Cannot add pollers before the RconClient has opened");
        }

        pollers.scheduleAtFixedRate(
                () -> playerPollingListener.onResult(rcon().info().getCurrentPlayers()),
                repeatInterval,
                repeatInterval,
                timeUnit
        );
    }

    private IEventListener<RconMessageEvent> asyncRequestListener()
    {
        return event ->
        {
            final CompletableFuture<RconReceive> completableFuture = asyncRequests.getIfPresent(event.getRconReceive().getIdentifier());

            if (completableFuture != null)
            {
                completableFuture.complete(event.getRconReceive());
            }
        };
    }

    private void handleRconRaw(String rconRaw)
    {
        final Optional<RconReceive> rconReceiveOptional = tryDeserializeRconMessage(rconRaw);
        rconReceiveOptional.ifPresent(handleRconReceive());
    }

    private Consumer<RconReceive> handleRconReceive()
    {
        return rconReceive ->
        {
            emitEvent().accept(new RconMessageEvent(rconReceive));

            final Optional<RconMessageTypes> rconMessageType = getRconMessageParser().parseMessage().apply(rconReceive.getMessage());
            rconMessageType.ifPresent(tryParseRconMessage(rconReceive.getMessage()));
        };
    }

    private Consumer<RconMessageTypes> tryParseRconMessage(String rconMessage)
    {
        return rconMessageType -> getEventParser(rconMessageType).ifPresent(doParseRconMessage(rconMessage));
    }

    private Consumer<IEventParser<BaseRustEvent>> doParseRconMessage(String rconMessage)
    {
        return eventParser -> eventParser.safeParseEvent().apply(rconMessage).ifPresent(emitEvent());
    }

    private Consumer<BaseRustEvent> emitEvent()
    {
        return getEventBus()::emitEvent;
    }

    private Optional<RconReceive> tryDeserializeRconMessage(String rconMessage)
    {
        try
        {
            final RconReceive rconReceive = getSerializer().deserialize(rconMessage, RconReceive.class);
            return Optional.ofNullable(rconReceive);
        }
        catch (Exception e)
        {
            return Optional.empty();
        }
    }

    private IEventListener<RconErrorEvent> rconErrorListener()
    {
        return event ->
        {
            getLogger().error(event.getException().getMessage());

            if (registerDebugListeners)
            {
                event.getException().printStackTrace();
            }
        };
    }

    private IEventListener<RconClosedEvent> rconClosedListener()
    {
        return event -> close();
    }

    private static class InternalWebSocketListener implements IWebSocketListener
    {
        private RustClient rustClient;

        @Override
        public void onOpen()
        {
            emit(new RconOpenEvent());
        }

        @Override
        public void onMessage(String message)
        {
            log(message);
            rustClient.handleRconRaw(message);
        }

        @Override
        public void onClose(int code, String reason)
        {
            emit(new RconClosedEvent(code, reason));
        }

        @Override
        public void onError(Exception e)
        {
            emit(new RconErrorEvent(e));
        }

        private void log(String message)
        {
            rustClient.getLogger().debug(message);
        }

        private void emit(BaseRustEvent rustEvent)
        {
            rustClient.getEventBus().emitEvent(rustEvent);
        }

        private void setRustClient(RustClient rustClient)
        {
            this.rustClient = rustClient;
        }
    }

    public static RustClientBuilder builder()
    {
        return new RustClientBuilder();
    }

    public static class RustClientBuilder
    {
        private ILogger logger;
        private ISerializer serializer;
        private IWebSocketListener webSocketListener;
        private IEventBus eventBus;
        private IRconMessageParser rconMessageParser;

        private String hostname;
        private String password;
        private int port;

        private boolean debugMode;
        private boolean quietMode;

        RustClientBuilder()
        {
            this.logger = new DefaultLogger(RustClient.class);
            this.serializer = new DefaultSerializer();
            this.webSocketListener = new InternalWebSocketListener();
            this.eventBus = new DefaultEventBus();
            this.rconMessageParser = new DefaultRconMessageParser();
            this.debugMode = false;
            this.quietMode = false;
        }

        public RustClientBuilder connectTo(String hostname, String password)
        {
            return this.connectTo(hostname, password, DEFAULT_PORT);
        }

        public RustClientBuilder connectTo(String hostname, String password, int port)
        {
            if (port <= 1023) throw new IllegalArgumentException("Port number inside reserved range (0 - 1023)");
            if (port > 65535) throw new IllegalArgumentException("Port number outside range (1024 - 65535)");
            Objects.requireNonNull(hostname, "Hostname cannot be null");
            Objects.requireNonNull(password, "Password cannot be null");

            this.hostname = hostname;
            this.password = password;
            this.port = port;
            return this;
        }

        public RustClientBuilder withLogger(ILogger logger)
        {
            Objects.requireNonNull(logger, "ILogger cannot be null");

            this.logger = logger;
            return this;
        }

        public RustClientBuilder withoutLogger()
        {
            this.logger = Constants.noOpLogger();
            return this;
        }

        public RustClientBuilder withSerializer(ISerializer serializer)
        {
            Objects.requireNonNull(serializer, "ISerializer cannot be null");

            this.serializer = serializer;
            return this;
        }

        public RustClientBuilder withWebSocketListener(IWebSocketListener webSocketListener)
        {
            Objects.requireNonNull(webSocketListener, "IWebSocketListener cannot be null");

            this.webSocketListener = webSocketListener;
            return this;
        }

        public RustClientBuilder withEventBus(IEventBus eventBus)
        {
            Objects.requireNonNull(eventBus, "IEventBus cannot be null");

            this.eventBus = eventBus;
            return this;
        }

        public RustClientBuilder withRconMessageParser(IRconMessageParser rconMessageParser)
        {
            Objects.requireNonNull(rconMessageParser, "IRconMessageParser cannot be null");

            this.rconMessageParser = rconMessageParser;
            return this;
        }

        public RustClientBuilder debugMode()
        {
            this.debugMode = true;
            return this;
        }

        public RustClientBuilder quietMode()
        {
            this.quietMode = true;
            return this;
        }

        public RustClient build()
        {
            return this.build(DefaultWebSocketClient.usingCredentialsAndListener(hostname, password, port, webSocketListener));
        }

        public RustClient build(IWebSocketClient webSocketClient)
        {
            Objects.requireNonNull(webSocketClient, "IWebSocketClient cannot be null");
            Objects.requireNonNull(webSocketListener, "IWebSocketListener cannot be null");

            final RustClient rustClient =
                    new RustClient(logger, serializer, webSocketClient, webSocketListener, eventBus, rconMessageParser, debugMode);

            return postProcess(rustClient);
        }

        private RustClient postProcess(RustClient rustClient)
        {
            if (debugMode)
            {
                rustClient.getLogger().logLevelEnabled(LogLevels.DEBUG, true);
            }

            if (quietMode)
            {
                rustClient.setLoggingEnabled(false);
            }

            return rustClient;
        }
    }
}