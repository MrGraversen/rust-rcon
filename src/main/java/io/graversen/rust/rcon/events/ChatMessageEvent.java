package io.graversen.rust.rcon.events;

import io.graversen.fiber.event.common.BaseEvent;

public class ChatMessageEvent extends BaseEvent
{
    private final String playerName;
    private final String steamId64;
    private final String chatMessage;

    public ChatMessageEvent(String playerName, String steamId64, String chatMessage)
    {
        this.playerName = playerName;
        this.steamId64 = steamId64;
        this.chatMessage = chatMessage;
    }

    public String getPlayerName()
    {
        return playerName;
    }

    public String getSteamId64()
    {
        return steamId64;
    }

    public String getChatMessage()
    {
        return chatMessage;
    }
}
