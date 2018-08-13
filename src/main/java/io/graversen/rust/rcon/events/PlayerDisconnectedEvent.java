package io.graversen.rust.rcon.events;

import io.graversen.fiber.event.common.BaseEvent;

public class PlayerDisconnectedEvent extends BaseEvent
{
    private final String ipAddress;
    private final String steamId64;
    private final String playerName;
    private final String reason;

    public PlayerDisconnectedEvent(String ipAddress, String steamId64, String playerName, String reason)
    {
        this.ipAddress = ipAddress;
        this.steamId64 = steamId64;
        this.playerName = playerName;
        this.reason = reason;
    }

    public String getIpAddress()
    {
        return ipAddress;
    }

    public String getSteamId64()
    {
        return steamId64;
    }

    public String getPlayerName()
    {
        return playerName;
    }

    public String getReason()
    {
        return reason;
    }
}
