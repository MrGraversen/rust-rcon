package io.graversen.rust.rcon.events;

import io.graversen.fiber.event.common.BaseEvent;

public class ChatMessageEvent extends BaseEvent
{
    private final String steamId;
    private final String steamId64;
    private final String chatMessage;

    public ChatMessageEvent(String steamId, String steamId64, String chatMessage)
    {
        this.steamId = steamId;
        this.steamId64 = steamId64;
        this.chatMessage = chatMessage;
    }
}
