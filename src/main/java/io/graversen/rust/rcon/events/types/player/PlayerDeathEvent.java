package io.graversen.rust.rcon.events.types.player;

public class PlayerDeathEvent extends BasePlayerEvent
{
    private final String killed;
    private final String killer;
    private final boolean killerIsPlayer;

    public PlayerDeathEvent(String playerName, String steamId64, String killed, String killer, boolean killerIsPlayer)
    {
        super(playerName, steamId64);
        this.killed = killed;
        this.killer = killer;
        this.killerIsPlayer = killerIsPlayer;
    }

    public String getKilled()
    {
        return killed;
    }
}
