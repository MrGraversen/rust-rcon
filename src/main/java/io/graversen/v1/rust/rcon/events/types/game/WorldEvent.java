package io.graversen.v1.rust.rcon.events.types.game;

public class WorldEvent extends BaseGameEvent
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
