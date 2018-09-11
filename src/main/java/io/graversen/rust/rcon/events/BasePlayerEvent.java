package io.graversen.rust.rcon.events;

import io.graversen.fiber.event.common.BaseEvent;

public abstract class BasePlayerEvent extends BaseEvent
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
