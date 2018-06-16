package io.graversen.rust.rcon.test;

import io.graversen.rust.rcon.ConsoleToEventDigester;
import io.graversen.rust.rcon.events.ChatMessageEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class ConsoleToEventDigesterTest
{
    private ConsoleToEventDigester consoleToEventDigester;

    @BeforeEach
    void setUp()
    {
        this.consoleToEventDigester = new ConsoleToEventDigester();
    }

    @Test
    void testDigestChatMessageEvent_validation()
    {
        final String[] chatMessages = {
                "[CHAT] Pope of the Nope[468295/76561197979952036] : ssss",
        };

        Arrays.stream(chatMessages).forEach(chatMessage -> consoleToEventDigester.digestChatMessageEvent(chatMessage));
    }

    @Test
    void testDigestChatMessageEvent_findCorrectInfo()
    {
        final String chatMessage1 = "[CHAT] Pope of the Nope[468295/76561197979952036] : ssss";
        final ChatMessageEvent chatMessageEvent1 = consoleToEventDigester.digestChatMessageEvent(chatMessage1);

        assertEquals("Pope of the Nope", chatMessageEvent1.getPlayerName());
        assertEquals("76561197979952036", chatMessageEvent1.getSteamId64());
        assertEquals("ssss", chatMessageEvent1.getChatMessage());
    }
}