package io.graversen.v1.rust.rcon.events.parsers;

import io.graversen.v1.rust.rcon.RconMessageTypes;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;

public class DefaultRconMessageParser implements IRconMessageParser
{
    @Override
    public Function<String, Optional<RconMessageTypes>> parseMessage()
    {
        return message -> Arrays.stream(RconMessageTypes.values())
                .filter(rconMessage -> deepMatches(message, rconMessage))
                .findFirst();
    }

    private boolean deepMatches(String consoleInput, RconMessageTypes rconMessage)
    {
        return rconMessage.matches(consoleInput) && nothingElse(rconMessage, consoleInput);
    }

    private boolean nothingElse(RconMessageTypes except, String consoleInput)
    {
        return Arrays.stream(RconMessageTypes.values())
                .filter(c -> !c.equals(except))
                .noneMatch(c -> c.matches(consoleInput));
    }
}
