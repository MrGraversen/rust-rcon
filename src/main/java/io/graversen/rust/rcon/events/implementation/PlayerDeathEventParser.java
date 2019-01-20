package io.graversen.rust.rcon.events.implementation;

import io.graversen.rust.rcon.events.IEventParser;
import io.graversen.rust.rcon.events.types.player.PlayerDeathEvent;

import java.util.Optional;
import java.util.function.Function;

public class PlayerDeathEventParser implements IEventParser<PlayerDeathEvent>
{
    @Override
    public Function<String, Optional<PlayerDeathEvent>> parseEvent()
    {
        throw new UnsupportedOperationException();
    }
}
