package io.graversen.rust.rcon.events;

public class PlayerDisconnectedEvent extends BasePlayerEvent
{
    private final String connectionTuple;
    private final String reason;

    public PlayerDisconnectedEvent(String connectionTuple, String steamId64, String playerName, String reason)
    {
        super(playerName, steamId64);
        this.connectionTuple = connectionTuple;
        this.reason = reason;
    }

    public String getConnectionTuple()
    {
        return connectionTuple;
    }

    public String getReason()
    {
        return reason;
    }
}
