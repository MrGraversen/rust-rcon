package io.graversen.rust.rcon.test;

import io.graversen.rust.rcon.RconMessages;
import io.graversen.rust.rcon.events.WorldEvent;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestDigestWorldEvent extends BaseDigesterTest
{
    @Test
    void test_validation()
    {
        final String[] eventMessages = {
                "[event] assets/prefabs/npc/cargo plane/cargo_plane.prefab",
                "[event] assets/prefabs/npc/ch47/ch47scientists.entity.prefab",
                "[event] assets/prefabs/npc/patrol helicopter/patrolhelicopter.prefab"
        };

        Arrays.stream(eventMessages).forEach(s -> consoleMessageDigester.validateEvent(s, RconMessages.WORLD_EVENT));
    }

    @Test
    void test_unknown()
    {
        final String eventString = "[event] wubba lubba dub dub";
        assertEquals(WorldEvent.EventTypes.UNKNOWN, consoleMessageDigester.digestWorldEvent(eventString).getEventType());
    }

    @Test
    void test_cargoPlane()
    {
        final String eventString = "[event] assets/prefabs/npc/cargo plane/cargo_plane.prefab";
        assertEquals(WorldEvent.EventTypes.CARGO_PLANE, consoleMessageDigester.digestWorldEvent(eventString).getEventType());
    }

    @Test
    void test_ch47()
    {
        final String eventString = "[event] assets/prefabs/npc/ch47/ch47scientists.entity.prefab";
        assertEquals(WorldEvent.EventTypes.CH47_SCIENTISTS, consoleMessageDigester.digestWorldEvent(eventString).getEventType());
    }

    @Test
    void test_patrolHelicopter()
    {
        final String eventString = "[event] assets/prefabs/npc/patrol helicopter/patrolhelicopter.prefab";
        assertEquals(WorldEvent.EventTypes.PATROL_HELICOPTER, consoleMessageDigester.digestWorldEvent(eventString).getEventType());
    }
}
