package io.graversen.v1.rust.rcon.events.types.player;

import io.graversen.v1.rust.rcon.events.types.BaseRustEvent;

public abstract class BasePlayerEvent extends BaseRustEvent
{
    private final String playerName;
    private final String steamId64;

    public BasePlayerEvent(String playerName, String steamId64)
    {
        this.playerName = playerName;
        this.steamId64 = steamId64;
    }

    public String getPlayerName()
    {
        return playerName;
    }

    public String getSteamId64()
    {
        return steamId64;
    }
}
