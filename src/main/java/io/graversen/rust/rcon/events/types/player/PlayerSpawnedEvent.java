package io.graversen.rust.rcon.events.types.player;

public class PlayerSpawnedEvent extends BasePlayerEvent
{
    public PlayerSpawnedEvent(String playerName, String steamId64)
    {
        super(playerName, steamId64);
    }
}
