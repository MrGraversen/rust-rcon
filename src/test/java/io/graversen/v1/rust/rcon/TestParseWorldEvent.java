package io.graversen.v1.rust.rcon;

import io.graversen.v1.rust.rcon.events.IEventParser;
import io.graversen.v1.rust.rcon.events.implementation.WorldEventParser;
import io.graversen.v1.rust.rcon.events.types.game.WorldEvent;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestParseWorldEvent extends BaseDefaultParserTest
{
    private final String[] messages = {
            "[event] assets/prefabs/npc/cargo plane/cargo_plane.prefab",
            "[event] assets/prefabs/npc/ch47/ch47scientists.entity.prefab",
            "[event] assets/prefabs/npc/patrol helicopter/patrolhelicopter.prefab"
    };

    private final IEventParser<WorldEvent> eventParser = new WorldEventParser();

    @Test
    void test_validation()
    {
        Arrays.stream(messages).map(eventParser.parseEvent()).forEach(event -> assertTrue(event.isPresent()));
    }

    @Test
    void test_unknown()
    {
        final String eventString = "[event] wubba lubba dub dub";
        final Optional<WorldEvent> event = eventParser.parseEvent().apply(eventString);

        assertTrue(event.isPresent());
        assertEquals(WorldEvent.EventTypes.UNKNOWN, event.get().getEventType());
    }

    @Test
    void test_cargoPlane()
    {
        final String eventString = "[event] assets/prefabs/npc/cargo plane/cargo_plane.prefab";
        final Optional<WorldEvent> event = eventParser.parseEvent().apply(eventString);

        assertTrue(event.isPresent());
        assertEquals(WorldEvent.EventTypes.CARGO_PLANE, event.get().getEventType());
    }

    @Test
    void test_ch47()
    {
        final String eventString = "[event] assets/prefabs/npc/ch47/ch47scientists.entity.prefab";
        final Optional<WorldEvent> event = eventParser.parseEvent().apply(eventString);

        assertTrue(event.isPresent());
        assertEquals(WorldEvent.EventTypes.CH47_SCIENTISTS, event.get().getEventType());
    }

    @Test
    void test_patrolHelicopter()
    {
        final String eventString = "[event] assets/prefabs/npc/patrol helicopter/patrolhelicopter.prefab";
        final Optional<WorldEvent> event = eventParser.parseEvent().apply(eventString);

        assertTrue(event.isPresent());
        assertEquals(WorldEvent.EventTypes.PATROL_HELICOPTER, event.get().getEventType());
    }
}
