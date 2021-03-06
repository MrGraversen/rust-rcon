package io.graversen.rust.rcon.events.implementation;

import io.graversen.rust.rcon.events.IEventParser;
import io.graversen.rust.rcon.events.types.game.SaveEvent;

import java.util.Optional;
import java.util.function.Function;

public class SaveEventParser implements IEventParser<SaveEvent>
{
    @Override
    public Function<String, Optional<SaveEvent>> parseEvent()
    {
        return rconMessage -> Optional.of(new SaveEvent());
    }
}
