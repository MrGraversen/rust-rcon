package io.graversen.rust.rcon.events.types.server;

import io.graversen.rust.rcon.objects.RconReceive;

public class RconMessageEvent extends BaseServerEvent
{
    private final RconReceive rconReceive;

    public RconMessageEvent(RconReceive rconReceive)
    {
        this.rconReceive = rconReceive;
    }

    public RconReceive getRconReceive()
    {
        return rconReceive;
    }
}
