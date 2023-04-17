package io.graversen.v1.rust.rcon.events.types;

import io.graversen.fiber.event.common.BaseEvent;

public abstract class BaseRustEvent extends BaseEvent
{
    public BaseRustEvent()
    {
        super(false);
    }
}
