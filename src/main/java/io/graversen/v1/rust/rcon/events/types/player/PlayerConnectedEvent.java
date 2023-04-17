package io.graversen.v1.rust.rcon.events.types.player;

public class PlayerConnectedEvent extends BasePlayerEvent
{
    private final String connectionTuple;
    private final String osDescriptor;

    public PlayerConnectedEvent(String connectionTuple, String osDescriptor, String steamId64, String playerName)
    {
        super(playerName, steamId64);
        this.connectionTuple = connectionTuple;
        this.osDescriptor = osDescriptor;
    }

    public String getConnectionTuple()
    {
        return connectionTuple;
    }

    public String getOsDescriptor()
    {
        return osDescriptor;
    }
}
