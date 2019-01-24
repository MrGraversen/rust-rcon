package io.graversen.rust.rcon.test;

import io.graversen.rust.rcon.events.IEventParser;
import io.graversen.rust.rcon.events.implementation.ChatMessageEventParser;
import io.graversen.rust.rcon.events.types.player.ChatMessageEvent;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestChatMessageEventParser extends BaseDefaultParserTest
{
    private static final String[] messages = {
            "[CHAT] Pope of the Nope[468295/76561197979952036] : ssss",
            "[CHAT] DarkDouchebag[1014803/76561198046357656] : Don't know waht to write :o",
            "[CHAT] DarkDouchebag[1014803/76561198046357656] : [Server] Kan jo ikke huske hvordan den laver det",
            "[CHAT] DarkDouchebag[1014803/76561198046357656] : [DarkDouchebag] [Server] Hello mate",
            "[CHAT] DarkDouchebag[1014803/76561198046357656] : PLain old text, PLain old text, PLain old text, PLain old text, PLain old text, PLain old text, PLai",
            "[CHAT] DarkDouchebag[1014803/76561198046357656] : [CHAT] dont know hwat to write \\:o :o",
            "[CHAT] DarkDouchebag[1014803/76561198046357656] : This message : will not work : haha"
    };

    private final IEventParser<ChatMessageEvent> eventParser = new ChatMessageEventParser();

    @Test
    void test_validation()
    {
        Arrays.stream(messages).map(eventParser.parseEvent()).forEach(event -> assertTrue(event.isPresent()));
    }

    @Test
    void test_findCorrectInfo_1()
    {
        final Optional<ChatMessageEvent> event = eventParser.parseEvent().apply(messages[0]);

        assertTrue(event.isPresent());
        assertEquals("ssss", event.get().getChatMessage());
        assertEquals("Pope of the Nope", event.get().getPlayerName());
        assertEquals("76561197979952036", event.get().getSteamId64());
    }

    @Test
    void test_findCorrectInfo_2()
    {
        final Optional<ChatMessageEvent> event = eventParser.parseEvent().apply(messages[1]);

        assertTrue(event.isPresent());
        assertEquals("Don't know waht to write :o", event.get().getChatMessage());
        assertEquals("DarkDouchebag", event.get().getPlayerName());
        assertEquals("76561198046357656", event.get().getSteamId64());
    }

    @Test
    void test_findCorrectInfo_3()
    {
        final Optional<ChatMessageEvent> event = eventParser.parseEvent().apply(messages[3]);

        assertTrue(event.isPresent());
        assertEquals("[DarkDouchebag] [Server] Hello mate", event.get().getChatMessage());
        assertEquals("DarkDouchebag", event.get().getPlayerName());
        assertEquals("76561198046357656", event.get().getSteamId64());
    }

    @Test
    void test_findCorrectInfo_4()
    {
        final Optional<ChatMessageEvent> event = eventParser.parseEvent().apply(messages[5]);

        assertTrue(event.isPresent());
        assertEquals("[CHAT] dont know hwat to write \\:o :o", event.get().getChatMessage());
        assertEquals("DarkDouchebag", event.get().getPlayerName());
        assertEquals("76561198046357656", event.get().getSteamId64());
    }

    @Test
    void test_findCorrectInfo_5()
    {
        final Optional<ChatMessageEvent> event = eventParser.parseEvent().apply(messages[6]);

        assertTrue(event.isPresent());
        assertEquals("This message : will not work : haha", event.get().getChatMessage());
        assertEquals("DarkDouchebag", event.get().getPlayerName());
        assertEquals("76561198046357656", event.get().getSteamId64());
    }
}
