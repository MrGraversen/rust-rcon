package io.graversen.v1.rust.rcon.events;

import io.graversen.v1.rust.rcon.RconMessageTypes;
import io.graversen.v1.rust.rcon.events.types.BaseRustEvent;
import io.graversen.v1.rust.rcon.events.types.custom.PlayerDeathEvent;
import io.graversen.v1.rust.rcon.events.types.game.SaveEvent;
import io.graversen.v1.rust.rcon.events.types.game.WorldEvent;
import io.graversen.v1.rust.rcon.events.types.player.*;
import io.graversen.v1.rust.rcon.events.types.player.*;

public enum RustEvents
{
    CHAT_EVENT(ChatMessageEvent.class, RconMessageTypes.CHAT),
    PLAYER_CONNECTED_EVENT(PlayerConnectedEvent.class, RconMessageTypes.PLAYER_CONNECTED),
    PLAYER_DISCONNECTED_EVENT(PlayerDisconnectedEvent.class, RconMessageTypes.PLAYER_DISCONNECTED),
    PLAYER_DEATH_EVENT(PlayerDeathEvent.class, RconMessageTypes.PLAYER_DEATH),
    PLAYER_SPAWNED_EVENT(PlayerSpawnedEvent.class, RconMessageTypes.PLAYER_SPAWNED),
    PLAYER_SUICIDE_EVENT(PlayerSuicideEvent.class, RconMessageTypes.SUICIDE_EVENT),
    WORLD_EVENT(WorldEvent.class, RconMessageTypes.WORLD_EVENT),
    SAVE_EVENT(SaveEvent.class, RconMessageTypes.SAVE_EVENT);

    private final Class<? extends BaseRustEvent> eventClass;
    private final RconMessageTypes rconMessageType;

    RustEvents(Class<? extends BaseRustEvent> eventClass, RconMessageTypes rconMessageType)
    {
        this.eventClass = eventClass;
        this.rconMessageType = rconMessageType;
    }

    public Class<? extends BaseRustEvent> eventClass()
    {
        return eventClass;
    }

    public RconMessageTypes rconMessageType()
    {
        return rconMessageType;
    }
}
