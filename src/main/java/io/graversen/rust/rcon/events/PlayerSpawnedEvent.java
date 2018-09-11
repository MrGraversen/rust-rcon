package io.graversen.rust.rcon.events;

public class PlayerSpawnedEvent extends BasePlayerEvent
{
    public PlayerSpawnedEvent(String playerName, String steamId64)
    {
        super(playerName, steamId64);
    }
}
