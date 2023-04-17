package io.graversen.v1.rust.rcon.events.types.player;

public class PlayerSpawnedEvent extends BasePlayerEvent
{
    public PlayerSpawnedEvent(String playerName, String steamId64)
    {
        super(playerName, steamId64);
    }
}
