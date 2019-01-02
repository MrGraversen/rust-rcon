package io.graversen.rust.rcon.events;

import io.graversen.fiber.event.common.BaseEvent;

import java.util.function.Function;

public interface IEventParser<T extends BaseEvent>
{
    Function<String, T> parseEvent(String consoleMessage);
}
