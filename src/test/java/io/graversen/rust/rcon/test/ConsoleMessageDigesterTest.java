package io.graversen.rust.rcon.test;

import io.graversen.rust.rcon.ConsoleMessageDigester;
import io.graversen.rust.rcon.events.ChatMessageEvent;
import io.graversen.rust.rcon.events.ServerEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class ConsoleMessageDigesterTest {
    private ConsoleMessageDigester consoleMessageDigester;

    @BeforeEach
    void setUp() {
        this.consoleMessageDigester = new ConsoleMessageDigester();
    }

    @Test
    void testDigestChatMessageEvent_validation() {
        final String[] chatMessages = {
                "[CHAT] Pope of the Nope[468295/76561197979952036] : ssss",
        };

        Arrays.stream(chatMessages).forEach(chatMessage -> consoleMessageDigester.digestChatMessageEvent(chatMessage));
    }

    @Test
    void testDigestChatMessageEvent_findCorrectInfo() {
        final String chatMessage1 = "[CHAT] Pope of the Nope[468295/76561197979952036] : ssss";
        final ChatMessageEvent chatMessageEvent1 = consoleMessageDigester.digestChatMessageEvent(chatMessage1);

        assertEquals("Pope of the Nope", chatMessageEvent1.getPlayerName());
        assertEquals("76561197979952036", chatMessageEvent1.getSteamId64());
        assertEquals("ssss", chatMessageEvent1.getChatMessage());
    }

    @Test
    void testDigestServerEvent_allEventTypes() {
        final String[] eventMessages = {
                "[event] assets/prefabs/npc/cargo plane/cargo_plane.prefab",
                "[event] assets/prefabs/npc/ch47/ch47scientists.entity.prefab",
                "[event] assets/prefabs/npc/patrol helicopter/patrolhelicopter.prefab"
        };

        Arrays.stream(eventMessages).forEach(s -> consoleMessageDigester.digest(s).orElseThrow(() -> new RuntimeException("Could not digest event message")));

        assertEquals(ServerEvent.EventTypes.CARGO_PLANE, consoleMessageDigester.digestServerEvent(eventMessages[0]).getEventType());
        assertEquals(ServerEvent.EventTypes.CH47_SCIENTISTS, consoleMessageDigester.digestServerEvent(eventMessages[1]).getEventType());
        assertEquals(ServerEvent.EventTypes.PATROL_HELICOPTER, consoleMessageDigester.digestServerEvent(eventMessages[2]).getEventType());
    }
}