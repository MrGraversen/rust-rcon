package io.graversen.rust.rcon.events.types.player;

import io.graversen.rust.rcon.objects.util.SuicideCauses;

public class PlayerSuicideEvent extends BasePlayerEvent
{
    private final SuicideCauses suicideCause;

    public PlayerSuicideEvent(String playerName, String steamId64, SuicideCauses suicideCause)
    {
        super(playerName, steamId64);
        this.suicideCause = suicideCause;
    }

    public SuicideCauses getSuicideCause()
    {
        return suicideCause;
    }
}
