package io.graversen.rust.rcon.events.types;

public class WorldEvent extends BaseRustEvent
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
