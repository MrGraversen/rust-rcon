package io.graversen.rust.rcon.rustclient;

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
import io.graversen.rust.rcon.events.types.server.RconClosedEvent;
import io.graversen.rust.rcon.events.types.server.RconErrorEvent;
import io.graversen.rust.rcon.events.types.server.RconOpenEvent;
import io.graversen.rust.rcon.logging.DefaultLogger;
import io.graversen.rust.rcon.logging.ILogger;
import io.graversen.rust.rcon.serialization.DefaultSerializer;
import io.graversen.rust.rcon.websocket.DefaultWebSocketClient;
import io.graversen.rust.rcon.websocket.IWebSocketClient;
import io.graversen.rust.rcon.websocket.IWebSocketListener;
import io.graversen.trunk.io.serialization.interfaces.ISerializer;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

public class RustClient implements AutoCloseable
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
    }

    public void open()
    {
        if (open)
        {
            throw new RconException("RustClient has already been opened");
        }

        if (this.webSocketListener instanceof InternalWebSocketListener)
        {
            ((InternalWebSocketListener) this.webSocketListener).setRustClient(this);
        }

        Arrays.stream(DEFAULT_EVENT_CLASSES).forEach(
                eventClass -> eventBus.registerEventListener(eventClass, event -> getLogger().info(event.getClass().getSimpleName()))
        );

        this.eventBus.start();
        this.webSocketClient.open();

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
                this.webSocketClient.close();
                this.eventBus.stop();
            }
        }
        catch (Exception e)
        {
            // ¯\_(ツ)_/¯
        }
    }

    private void handleRconMessage(String rconMessage)
    {
        System.out.println("RustClient.handleRconMessage");
        final Optional<RconMessageTypes> rconMessageTypeOptional = getRconMessageParser().parseMessage().apply(rconMessage);
        rconMessageTypeOptional.ifPresent(tryParseRconMessage(rconMessage));
    }

    private Consumer<RconMessageTypes> tryParseRconMessage(String rconMessage)
    {
        System.out.println("RustClient.tryParseRconMessage");
        return rconMessageType -> getEventParser(rconMessageType).ifPresent(doParseRconMessage(rconMessage));
    }

    private Consumer<IEventParser<BaseRustEvent>> doParseRconMessage(String rconMessage)
    {
        System.out.println("RustClient.doParseRconMessage");
        return eventParser -> eventParser.safeParseEvent().apply(rconMessage).ifPresent(emitEvent());
    }

    private Consumer<BaseRustEvent> emitEvent()
    {
        System.out.println("RustClient.emitEvent");
        return getEventBus()::emitEvent;
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
            rustClient.handleRconMessage(message);
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