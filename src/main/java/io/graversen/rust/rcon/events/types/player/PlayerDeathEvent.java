package io.graversen.rust.rcon.events.types.player;

import io.graversen.rust.rcon.events.types.player.BasePlayerEvent;

public class PlayerDeathEvent extends BasePlayerEvent
{
    private final String targetEntityName;

    public PlayerDeathEvent(String playerName, String steamId64, String targetEntityName)
    {
        super(playerName, steamId64);
        this.targetEntityName = targetEntityName;
    }

    public String getTargetEntityName()
    {
        return targetEntityName;
    }
}
