package io.graversen.rust.rcon.events;

public class ChatMessageEvent extends BasePlayerEvent
{
    private final String chatMessage;

    public ChatMessageEvent(String playerName, String steamId64, String chatMessage)
    {
        super(playerName, steamId64);
        this.chatMessage = chatMessage;
    }

    public String getChatMessage()
    {
        return chatMessage;
    }
}
