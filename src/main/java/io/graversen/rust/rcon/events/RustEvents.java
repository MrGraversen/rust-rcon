package io.graversen.rust.rcon.events;

import io.graversen.rust.rcon.RconMessageTypes;
import io.graversen.rust.rcon.events.types.BaseRustEvent;
import io.graversen.rust.rcon.events.types.custom.PlayerDeathEvent;
import io.graversen.rust.rcon.events.types.game.SaveEvent;
import io.graversen.rust.rcon.events.types.game.WorldEvent;
import io.graversen.rust.rcon.events.types.player.ChatMessageEvent;
import io.graversen.rust.rcon.events.types.player.PlayerConnectedEvent;
import io.graversen.rust.rcon.events.types.player.PlayerDisconnectedEvent;
import io.graversen.rust.rcon.events.types.player.PlayerSpawnedEvent;

public enum RustEvents
{
    CHAT_EVENT(ChatMessageEvent.class, RconMessageTypes.CHAT),
    PLAYER_CONNECTED_EVENT(PlayerConnectedEvent.class, RconMessageTypes.PLAYER_CONNECTED),
    PLAYER_DISCONNECTED_EVENT(PlayerDisconnectedEvent.class, RconMessageTypes.PLAYER_DISCONNECTED),
    PLAYER_DEATH_EVENT(PlayerDeathEvent.class, RconMessageTypes.PLAYER_DEATH),
    PLAYER_SPAWNED_EVENT(PlayerSpawnedEvent.class, RconMessageTypes.PLAYER_SPAWNED),
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
