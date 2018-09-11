package io.graversen.rust.rcon.test;

import io.graversen.rust.rcon.ConsoleDigests;
import io.graversen.rust.rcon.events.ChatMessageEvent;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class TestDigestChatMessageEvent extends BaseDigesterTest
{
    private final String[] chatMessages = {
            "[CHAT] Pope of the Nope[468295/76561197979952036] : ssss",
            "[CHAT] DarkDouchebag[1014803/76561198046357656] : Don't know waht to write :o",
            "[CHAT] DarkDouchebag[1014803/76561198046357656] : [Server] Kan jo ikke huske hvordan den laver det",
            "[CHAT] DarkDouchebag[1014803/76561198046357656] : [DarkDouchebag] [Server] Hello mate",
            "[CHAT] DarkDouchebag[1014803/76561198046357656] : PLain old text, PLain old text, PLain old text, PLain old text, PLain old text, PLain old text, PLai",
            "[CHAT] DarkDouchebag[1014803/76561198046357656] : [CHAT] dont know hwat to write \\:o :o"
    };

    @Test
    void test_validation()
    {
        Arrays.stream(chatMessages).forEach(s -> consoleMessageDigester.validateEvent(s, ConsoleDigests.CHAT));
    }

    @Test
    void test_findCorrectInfo_1()
    {
        final ChatMessageEvent chatMessageEvent = consoleMessageDigester.digestChatMessageEvent(chatMessages[0]);

        assertEquals("ssss", chatMessageEvent.getChatMessage());
        assertEquals("Pope of the Nope", chatMessageEvent.getPlayerName());
        assertEquals("76561197979952036", chatMessageEvent.getSteamId64());
    }

    @Test
    void test_findCorrectInfo_2()
    {
        final ChatMessageEvent chatMessageEvent = consoleMessageDigester.digestChatMessageEvent(chatMessages[1]);

        assertEquals("Don't know waht to write :o", chatMessageEvent.getChatMessage());
        assertEquals("DarkDouchebag", chatMessageEvent.getPlayerName());
        assertEquals("76561198046357656", chatMessageEvent.getSteamId64());
    }

    @Test
    void test_findCorrectInfo_3()
    {
        final ChatMessageEvent chatMessageEvent = consoleMessageDigester.digestChatMessageEvent(chatMessages[3]);

        assertEquals("[DarkDouchebag] [Server] Hello mate", chatMessageEvent.getChatMessage());
        assertEquals("DarkDouchebag", chatMessageEvent.getPlayerName());
        assertEquals("76561198046357656", chatMessageEvent.getSteamId64());
    }

    @Test
    void test_findCorrectInfo_4()
    {
        final ChatMessageEvent chatMessageEvent = consoleMessageDigester.digestChatMessageEvent(chatMessages[5]);

        assertEquals("[CHAT] dont know hwat to write \\:o :o", chatMessageEvent.getChatMessage());
        assertEquals("DarkDouchebag", chatMessageEvent.getPlayerName());
        assertEquals("76561198046357656", chatMessageEvent.getSteamId64());
    }
}
