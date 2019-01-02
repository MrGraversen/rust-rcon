package io.graversen.rust.rcon.events;

import io.graversen.fiber.event.bus.IEventBus;
import io.graversen.rust.rcon.IRconClient;

public class RconEvents
{
    private RconEvents()
    {

    }

    public static RconEvents using(IRconClient rconClient, IEventBus eventBus)
    {
        return new RconEvents();
    }
}
