package io.graversen.rust.rcon.events;

import io.graversen.fiber.event.common.BaseEvent;

public class PlayerDisconnectedEvent extends BaseEvent
{
    private final String steamId64;
    private final String playerName;
    private final String reason;

    public PlayerDisconnectedEvent(String steamId64, String playerName, String reason)
    {
        this.steamId64 = steamId64;
        this.playerName = playerName;
        this.reason = reason;
    }
}
