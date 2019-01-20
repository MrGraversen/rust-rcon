package io.graversen.rust.rcon.events.implementation;

import io.graversen.rust.rcon.events.IEventParser;
import io.graversen.rust.rcon.events.types.game.WorldEvent;

import java.util.Optional;
import java.util.function.Function;

public class WorldEventParser implements IEventParser<WorldEvent>
{
    @Override
    public Function<String, Optional<WorldEvent>> parseEvent()
    {
        return consoleMessage ->
        {
            if (consoleMessage.equalsIgnoreCase("[event] assets/prefabs/npc/cargo plane/cargo_plane.prefab"))
            {
                return Optional.of(new WorldEvent(WorldEvent.EventTypes.CARGO_PLANE));
            }
            else if (consoleMessage.equalsIgnoreCase("[event] assets/prefabs/npc/ch47/ch47scientists.entity.prefab"))
            {
                return Optional.of(new WorldEvent(WorldEvent.EventTypes.CH47_SCIENTISTS));
            }
            else if (consoleMessage.equalsIgnoreCase("[event] assets/prefabs/npc/patrol helicopter/patrolhelicopter.prefab"))
            {
                return Optional.of(new WorldEvent(WorldEvent.EventTypes.PATROL_HELICOPTER));
            }
            else
            {
                return Optional.of(new WorldEvent(WorldEvent.EventTypes.UNKNOWN));
            }
        };
    }
}
