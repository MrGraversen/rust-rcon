package io.graversen.rust.rcon.test;

import io.graversen.rust.rcon.RconMessages;
import io.graversen.rust.rcon.events.SaveEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestDigestSaveEvent extends BaseDigesterTest
{
    private final String EVENT_STRING = "Saving complete";

    @Test
    void test_validation()
    {
        consoleMessageDigester.validateEvent(EVENT_STRING, RconMessages.SAVE_EVENT);
    }

    @Test
    void test_parse()
    {
        final SaveEvent saveEvent = consoleMessageDigester.digestSaveEvent(EVENT_STRING);
        Assertions.assertNotNull(saveEvent);
    }
}