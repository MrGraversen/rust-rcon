package io.graversen.rust.rcon.events;

import io.graversen.fiber.event.common.BaseEvent;

public class WorldEvent extends BaseEvent
{
    private final EventTypes eventType;

    public WorldEvent(EventTypes eventType)
    {
        this.eventType = eventType;
    }

    public EventTypes getEventType()
    {
        return eventType;
    }

    public enum EventTypes
    {
        CARGO_PLANE,
        CH47_SCIENTISTS,
        PATROL_HELICOPTER,
        UNKNOWN
    }
}
