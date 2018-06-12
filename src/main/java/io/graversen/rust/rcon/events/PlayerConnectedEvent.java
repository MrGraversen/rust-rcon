package io.graversen.rust.rcon.events;

import io.graversen.fiber.event.common.BaseEvent;

public class PlayerConnectedEvent extends BaseEvent
{
    private final String connectionTuple;
    private final String osDescriptor;
    private final String steamId64;
    private final String playerName;

    public PlayerConnectedEvent(String connectionTuple, String osDescriptor, String steamId64, String playerName)
    {
        this.connectionTuple = connectionTuple;
        this.osDescriptor = osDescriptor;
        this.steamId64 = steamId64;
        this.playerName = playerName;
    }
}
