package io.graversen.v1.rust.rcon.listeners;

import io.graversen.v1.rust.rcon.events.types.custom.PlayerDeathEvent;
import io.graversen.v1.rust.rcon.events.types.game.WorldEvent;
import io.graversen.v1.rust.rcon.events.types.player.ChatMessageEvent;
import io.graversen.v1.rust.rcon.events.types.player.PlayerConnectedEvent;
import io.graversen.v1.rust.rcon.events.types.player.PlayerDisconnectedEvent;

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
