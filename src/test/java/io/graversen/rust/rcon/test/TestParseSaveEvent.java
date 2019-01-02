package io.graversen.rust.rcon.test;

import io.graversen.rust.rcon.RconMessages;
import io.graversen.rust.rcon.events.SaveEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestParseSaveEvent extends BaseDefaultParserTest
{
    private final String EVENT_STRING = "Saving complete";

    @Test
    void test_validation()
    {
        defaultConsoleParser.validateEvent(EVENT_STRING, RconMessages.SAVE_EVENT);
    }

    @Test
    void test_parse()
    {
        final SaveEvent saveEvent = defaultConsoleParser.parseSaveEvent(EVENT_STRING);
        Assertions.assertNotNull(saveEvent);
    }
}
