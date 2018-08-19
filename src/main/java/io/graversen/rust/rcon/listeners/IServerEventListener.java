package io.graversen.rust.rcon.listeners;

import io.graversen.rust.rcon.events.*;

public interface IServerEventListener
{
    void onChatMessage(ChatMessageEvent event);

    void onPlayerConnected(PlayerConnectedEvent event);

    void onPlayerDisconnected(PlayerDisconnectedEvent event);

    void onPlayerDeath(PlayerDeathEvent event);

    void onWorldEvent(WorldEvent event);
}
