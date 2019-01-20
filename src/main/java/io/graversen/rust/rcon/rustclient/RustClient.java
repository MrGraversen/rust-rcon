package io.graversen.rust.rcon.rustclient;

import io.graversen.fiber.event.bus.DefaultEventBus;
import io.graversen.fiber.event.bus.IEventBus;
import io.graversen.rust.rcon.events.types.BaseRustEvent;
import io.graversen.rust.rcon.events.types.server.RconClosedEvent;
import io.graversen.rust.rcon.events.types.server.RconErrorEvent;
import io.graversen.rust.rcon.events.types.server.RconOpenEvent;
import io.graversen.rust.rcon.logging.DefaultLogger;
import io.graversen.rust.rcon.logging.ILogger;
import io.graversen.rust.rcon.logging.NoOpLogger;
import io.graversen.rust.rcon.serialization.DefaultSerializer;
import io.graversen.rust.rcon.websocket.DefaultWebSocketClient;
import io.graversen.rust.rcon.websocket.IWebSocketClient;
import io.graversen.rust.rcon.websocket.IWebSocketListener;
import io.graversen.trunk.io.serialization.interfaces.ISerializer;

import java.util.Objects;

public class RustClient implements AutoCloseable
{
    private static final int DEFAULT_PORT = 25575;

    private final ILogger logger;
    private final ISerializer serializer;
    private final IWebSocketClient webSocketClient;
    private final IWebSocketListener webSocketListener;
    private final IEventBus eventBus;

    private RustClient(ILogger logger, ISerializer serializer, IWebSocketClient webSocketClient, IWebSocketListener webSocketListener, IEventBus eventBus)
    {
        this.logger = logger;
        this.serializer = serializer;
        this.webSocketClient = webSocketClient;
        this.webSocketListener = webSocketListener;
        this.eventBus = eventBus;
    }

    public void open()
    {
        this.eventBus.start();
        this.webSocketClient.open();

        if (this.webSocketListener instanceof InternalWebSocketListener)
        {
            ((InternalWebSocketListener) this.webSocketListener).setRustClient(this);
        }
    }

    public ILogger getLogger()
    {
        return logger;
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

    @Override
    public void close() throws Exception
    {
        this.webSocketClient.close();
        this.eventBus.stop();
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

        private void emit(BaseRustEvent rustEvent)
        {
            rustClient.getEventBus().emitEvent(rustEvent);
        }

        private void setRustClient(RustClient rustClient)
        {
            this.rustClient = rustClient;
        }
    }

    private static RustClientBuilder builder()
    {
        return new RustClientBuilder();
    }

    public static class RustClientBuilder
    {
        private ILogger logger;
        private ISerializer serializer;
        private IWebSocketListener webSocketListener;
        private IEventBus eventBus;

        private String hostname;
        private String password;
        private int port;

        RustClientBuilder()
        {
            this.logger = new DefaultLogger();
            this.serializer = new DefaultSerializer();
            this.webSocketListener = new InternalWebSocketListener();
            this.eventBus = new DefaultEventBus();
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
            this.logger = new NoOpLogger();
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

        public RustClient build()
        {
            return this.build(DefaultWebSocketClient.usingCredentialsAndListener(hostname, password, port, webSocketListener));
        }

        public RustClient build(IWebSocketClient webSocketClient)
        {
            Objects.requireNonNull(webSocketClient, "IWebSocketClient cannot be null");
            Objects.requireNonNull(webSocketListener, "IWebSocketListener cannot be null");

            return new RustClient(logger, serializer, webSocketClient, webSocketListener, eventBus);
        }
    }
}