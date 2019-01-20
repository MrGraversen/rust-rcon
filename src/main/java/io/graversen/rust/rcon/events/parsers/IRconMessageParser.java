package io.graversen.rust.rcon.events.parsers;

import io.graversen.rust.rcon.RconMessageTypes;

import java.util.Optional;
import java.util.function.Function;

@FunctionalInterface
public interface IRconMessageParser
{
    Function<String, Optional<RconMessageTypes>> parseMessage();
}
