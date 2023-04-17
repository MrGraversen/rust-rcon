package io.graversen.v1.rust.rcon.events.types.player;

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
