package io.graversen.rust.rcon.rustclient;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.graversen.fiber.event.bus.DefaultEventBus;
import io.graversen.fiber.event.bus.IEventBus;
import io.graversen.fiber.event.listeners.IEventListener;
import io.graversen.rust.rcon.Constants;
import io.graversen.rust.rcon.RconException;
import io.graversen.rust.rcon.RconMessageTypes;
import io.graversen.rust.rcon.events.IEventParser;
import io.graversen.rust.rcon.events.parsers.DefaultRconMessageParser;
import io.graversen.rust.rcon.events.parsers.IRconMessageParser;
import io.graversen.rust.rcon.events.types.BaseRustEvent;
import io.graversen.rust.rcon.events.types.server.RconMessageEvent;
import io.graversen.rust.rcon.events.types.server.RconClosedEvent;
import io.graversen.rust.rcon.events.types.server.RconErrorEvent;
import io.graversen.rust.rcon.events.types.server.RconOpenEvent;
import io.graversen.rust.rcon.logging.DefaultLogger;
import io.graversen.rust.rcon.logging.ILogger;
import io.graversen.rust.rcon.objects.RconReceive;
import io.graversen.rust.rcon.objects.RconRequest;
import io.graversen.rust.rcon.serialization.DefaultSerializer;
import io.graversen.rust.rcon.websocket.DefaultWebSocketClient;
import io.graversen.rust.rcon.websocket.IWebSocketClient;
import io.graversen.rust.rcon.websocket.IWebSocketListener;
import io.graversen.trunk.io.serialization.interfaces.ISerializer;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
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
    private final ConcurrentMap<RconMessageTypes, IEventParser<?>> eventParsers;

    private boolean open;
    private boolean loggingEnabled;
    private final AtomicInteger currentRequestCounter;
    private final Cache<Integer, CompletableFuture<RconReceive>> asyncRequests;

    private RustClient(
            ILogger logger,
            ISerializer serializer,
            IWebSocketClient webSocketClient,
            IWebSocketListener webSocketListener,
            IEventBus eventBus,
            IRconMessageParser rconMessageParser)
    {
        this.logger = logger;
        this.serializer = serializer;
        this.webSocketClient = webSocketClient;
        this.webSocketListener = webSocketListener;
        this.eventBus = eventBus;
        this.rconMessageParser = rconMessageParser;
        this.eventParsers = new ConcurrentHashMap<>();

        this.open = false;
        this.loggingEnabled = true;
        this.currentRequestCounter = new AtomicInteger(0);
        this.asyncRequests = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.HOURS).build();
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

    private void doSend(String rconMessage, int identifier)
    {
        final RconRequest rconRequest = new RconRequest(identifier, rconMessage, Constants.projectName());
        final String serializedPayload = getSerializer().serialize(rconRequest);

        getWebSocketClient().send(serializedPayload);
    }

    public void open()
    {
        if (open)
        {
            throw new RconException("RustClient has already been opened");
        }

        if (getWebSocketListener() instanceof InternalWebSocketListener)
        {
            ((InternalWebSocketListener) getWebSocketListener()).setRustClient(this);
        }

        Arrays.stream(DEFAULT_EVENT_CLASSES).forEach(
                eventClass -> getEventBus().registerEventListener(eventClass, event -> getLogger().info(event.getClass().getSimpleName()))
        );

        eventBus.registerEventListener(RconMessageEvent.class, this::asyncRequestListener);

        getEventBus().start();
        getWebSocketClient().open();

        this.open = true;
    }

    public <T extends BaseRustEvent> void addEventHandling(Class<T> eventClass, RconMessageTypes rconMessage, IEventParser<T> eventParser, IEventListener<T> eventListener)
    {
        eventParsers.put(rconMessage, eventParser);
        getEventBus().registerEventListener(eventClass, eventListener);
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
        this.loggingEnabled = loggingEnabled;
    }

    @Override
    public void close()
    {
        try
        {
            if (open)
            {
                getWebSocketClient().close();
                getEventBus().stop();
            }
        }
        catch (Exception e)
        {
            // ¯\_(ツ)_/¯
        }
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
            rustClient.getLogger().info(message);
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

        RustClientBuilder()
        {
            this.logger = new DefaultLogger();
            this.serializer = new DefaultSerializer();
            this.webSocketListener = new InternalWebSocketListener();
            this.eventBus = new DefaultEventBus();
            this.rconMessageParser = new DefaultRconMessageParser();
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

        public RustClientBuilder withSrializer(ISerializer serializer)
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

        public RustClient build()
        {
            return this.build(DefaultWebSocketClient.usingCredentialsAndListener(hostname, password, port, webSocketListener));
        }

        public RustClient build(IWebSocketClient webSocketClient)
        {
            Objects.requireNonNull(webSocketClient, "IWebSocketClient cannot be null");
            Objects.requireNonNull(webSocketListener, "IWebSocketListener cannot be null");

            return new RustClient(logger, serializer, webSocketClient, webSocketListener, eventBus, rconMessageParser);
        }
    }
}