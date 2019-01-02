package io.graversen.rust.rcon.listeners;

import io.graversen.rust.rcon.events.types.*;

public interface IServerEventListener
{
    void onRconOpen();

    void onRconClosed(int code, String reason);

    void onRconError(Exception e);

    void onChatMessage(ChatMessageEvent event);

    void onPlayerConnected(PlayerConnectedEvent event);

    void onPlayerDisconnected(PlayerDisconnectedEvent event);

    void onPlayerDeath(PlayerDeathEvent event);

    void onWorldEvent(WorldEvent event);

    void onEventParseError(Exception e);
}
