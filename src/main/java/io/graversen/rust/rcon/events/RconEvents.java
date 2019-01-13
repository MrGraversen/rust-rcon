package io.graversen.rust.rcon.events;

import io.graversen.fiber.event.bus.IEventBus;
import io.graversen.fiber.event.listeners.IEventListener;
import io.graversen.rust.rcon.RconMessages;
import io.graversen.rust.rcon.events.types.BaseRustEvent;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class RconEvents
{
    private final IEventBus eventBus;
    private final ConcurrentMap<RconMessages, IEventParser<?>> eventParsers;
    private final ConcurrentMap<Class<?>, IEventListener<?>> eventListeners;

    public RconEvents(IEventBus eventBus)
    {
        this.eventBus = eventBus;
        this.eventParsers = new ConcurrentHashMap<>();
        this.eventListeners = new ConcurrentHashMap<>();
    }

    public <T extends BaseRustEvent> void parse(RconMessages rconMessage, IEventParser<T> eventParser)
    {
        eventParsers.put(rconMessage, eventParser);
    }

    public <T extends BaseRustEvent> void listen(Class<T> eventClass, IEventListener<T> eventListener)
    {
        this.eventListeners.put(eventClass, eventListener);
    }

    public <T extends BaseRustEvent> Optional<IEventParser<T>> eventParser(RconMessages rconMessage)
    {
        final IEventParser<T> eventParserOrNull = (IEventParser<T>) eventParsers.getOrDefault(rconMessage, null);
        return Optional.ofNullable(eventParserOrNull);
    }

    public <T extends BaseRustEvent> Optional<IEventListener<T>> eventListener(Class<T> eventClass)
    {
        final IEventListener<T> eventListenerOrNull = (IEventListener<T>) eventListeners.getOrDefault(eventClass, null);
        return Optional.ofNullable(eventListenerOrNull);
    }
}
