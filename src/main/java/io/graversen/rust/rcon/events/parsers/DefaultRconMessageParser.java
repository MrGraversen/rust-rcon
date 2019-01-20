package io.graversen.rust.rcon.events.parsers;

import io.graversen.rust.rcon.RconMessages;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;

public class DefaultRconMessageParser implements IRconMessageParser
{
    @Override
    public Function<String, Optional<RconMessages>> parseMessage()
    {
        return message -> Arrays.stream(RconMessages.values())
                .filter(rconMessage -> deepMatches(message, rconMessage))
                .findFirst();
    }

    private boolean deepMatches(String consoleInput, RconMessages rconMessage)
    {
        return rconMessage.matches(consoleInput) && nothingElse(rconMessage, consoleInput);
    }

    private boolean nothingElse(RconMessages except, String consoleInput)
    {
        return Arrays.stream(RconMessages.values())
                .filter(c -> !c.equals(except))
                .noneMatch(c -> c.matches(consoleInput));
    }
}
