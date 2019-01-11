package io.graversen.rust.rcon.events;

import io.graversen.rust.rcon.events.types.BaseRustEvent;

import java.util.function.Function;

public interface IEventParser<T extends BaseRustEvent>
{
    Function<String, T> parseEvent(String consoleMessage);
}
