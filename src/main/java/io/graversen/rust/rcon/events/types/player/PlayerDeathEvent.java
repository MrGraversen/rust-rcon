package io.graversen.rust.rcon.events.types.player;

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
