package io.graversen.rust.rcon;

import io.graversen.rust.rcon.events.IEventParser;
import io.graversen.rust.rcon.events.implementation.PlayerSuicideEventParser;
import io.graversen.rust.rcon.events.types.player.PlayerSpawnedEvent;
import io.graversen.rust.rcon.events.types.player.PlayerSuicideEvent;
import io.graversen.rust.rcon.objects.util.SuicideCauses;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestParsePlayerSuicideEvent extends BaseDefaultParserTest
{
    private static final String[] messages = {
            "Doctor Delete[9756538/76561197979952036] was suicide by Suicide",
            "Doctor Delete[9756538/76561197979952036] was suicide by Blunt",
            "Doctor Delete[9756538/76561197979952036] was suicide by Stab",
            "Doctor Delete[9756538/76561197979952036] was suicide by Explosion",
            "Doctor Delete[9756538/76561197979952036] was killed by fall!",
            "Doctor Delete[9756538/76561197979952036] was killed by Drowned",
            "Doctor Delete[9756538/76561197979952036] was killed by Hunger",
            "Doctor Delete[9756538/76561197979952036] was killed by Thirst",
            "Doctor Delete[9756538/76561197979952036] was suicide by Heat",
            "Doctor Delete[9756538/76561197979952036] was killed by Cold",
            "Doctor Delete[9756538/76561197979952036] died (Fall)",
            "Doctor Delete[9756538/76561197979952036] died (Bleeding)"
    };

    private final IEventParser<PlayerSuicideEvent> eventParser = new PlayerSuicideEventParser();

    @Test
    void test_matching()
    {
        Arrays.stream(messages).forEach(message -> assertTrue(RconMessageTypes.SUICIDE_EVENT.matches(message)));
    }

    @Test
    void test_validation()
    {
        Arrays.stream(messages).map(eventParser.parseEvent()).forEach(event -> assertTrue(event.isPresent()));
    }

    @Test
    void test_findCorrectInfo_1()
    {
        final Optional<PlayerSuicideEvent> event = eventParser.parseEvent().apply(messages[0]);

        assertTrue(event.isPresent());
        assertEquals("Doctor Delete", event.get().getPlayerName());
        assertEquals("76561197979952036", event.get().getSteamId64());
        assertEquals(SuicideCauses.SUICIDE, event.get().getSuicideCause());
    }

    @Test
    void test_findCorrectInfo_2()
    {
        final Optional<PlayerSuicideEvent> event = eventParser.parseEvent().apply(messages[4]);

        assertTrue(event.isPresent());
        assertEquals("Doctor Delete", event.get().getPlayerName());
        assertEquals("76561197979952036", event.get().getSteamId64());
        assertEquals(SuicideCauses.FALL, event.get().getSuicideCause());
    }

    @Test
    void test_findCorrectInfo_3()
    {
        final Optional<PlayerSuicideEvent> event = eventParser.parseEvent().apply(messages[10]);

        assertTrue(event.isPresent());
        assertEquals("Doctor Delete", event.get().getPlayerName());
        assertEquals("76561197979952036", event.get().getSteamId64());
        assertEquals(SuicideCauses.FALL, event.get().getSuicideCause());
    }

    @Test
    void test_findCorrectInfo_4()
    {
        final Optional<PlayerSuicideEvent> event = eventParser.parseEvent().apply(messages[5]);

        assertTrue(event.isPresent());
        assertEquals("Doctor Delete", event.get().getPlayerName());
        assertEquals("76561197979952036", event.get().getSteamId64());
        assertEquals(SuicideCauses.DROWN, event.get().getSuicideCause());
    }
}
