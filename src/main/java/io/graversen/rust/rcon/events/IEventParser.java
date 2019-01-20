package io.graversen.rust.rcon.events;

import io.graversen.rust.rcon.events.types.BaseRustEvent;

import java.util.Optional;
import java.util.function.Function;

@FunctionalInterface
public interface IEventParser<T extends BaseRustEvent>
{
    default Function<String, Optional<T>> safeParseEvent()
    {
        return eventString ->
        {
            try
            {
                return parseEvent().apply(eventString);
            }
            catch (Exception e)
            {
                return Optional.empty();
            }
        };
    }

    Function<String, Optional<T>> parseEvent();
}
