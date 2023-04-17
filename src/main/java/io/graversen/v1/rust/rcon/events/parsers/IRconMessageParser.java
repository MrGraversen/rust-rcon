package io.graversen.v1.rust.rcon.events.parsers;

import io.graversen.v1.rust.rcon.RconMessageTypes;

import java.util.Optional;
import java.util.function.Function;

@FunctionalInterface
public interface IRconMessageParser
{
    Function<String, Optional<RconMessageTypes>> parseMessage();
}
