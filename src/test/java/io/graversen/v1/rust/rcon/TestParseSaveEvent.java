package io.graversen.v1.rust.rcon;

import io.graversen.v1.rust.rcon.events.IEventParser;
import io.graversen.v1.rust.rcon.events.implementation.SaveEventParser;
import io.graversen.v1.rust.rcon.events.types.game.SaveEvent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestParseSaveEvent extends BaseDefaultParserTest
{
    private static final String EVENT_STRING = "Saving complete";

    private final IEventParser<SaveEvent> eventParser = new SaveEventParser();

    @Test
    void test_validation()
    {
        assertTrue(eventParser.parseEvent().apply(EVENT_STRING).isPresent());
    }
}
