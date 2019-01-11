package io.graversen.rust.rcon.events;

import io.graversen.fiber.event.bus.IEventBus;
import io.graversen.fiber.event.listeners.IEventListener;
import io.graversen.rust.rcon.IRconClient;
import io.graversen.rust.rcon.RconMessages;
import io.graversen.rust.rcon.events.types.BaseRustEvent;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class RconEvents
{
    private final IRconClient rconClient;
    private final IEventBus eventBus;
    private final ConcurrentMap<RconMessages, IEventParser<? extends BaseRustEvent>> eventParsers;
    private final ConcurrentMap<Class<? extends BaseRustEvent>, IEventListener<? extends BaseRustEvent>> eventListeners;

    private RconEvents(IRconClient rconClient, IEventBus eventBus)
    {
        this.rconClient = rconClient;
        this.eventBus = eventBus;
        this.eventParsers = new ConcurrentHashMap<>();
        this.eventListeners = new ConcurrentHashMap<>();
    }

    public static RconEvents using(IRconClient rconClient, IEventBus eventBus)
    {
        return new RconEvents(rconClient, eventBus);
    }

    public <T extends BaseRustEvent> void parse(RconMessages rconMessage, IEventParser<T> eventParser)
    {
        eventParsers.putIfAbsent(rconMessage, eventParser);
    }

    public <T extends BaseRustEvent> void listen(Class<T> eventClass, IEventListener<T>... eventListeners)
    {
        Arrays.stream(eventListeners).forEach(eventListener -> this.eventListeners.putIfAbsent(eventClass, eventListener));
    }
}
