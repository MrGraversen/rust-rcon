package io.graversen.rust.rcon;

import io.graversen.rust.rcon.events.ChatMessageEvent;
import io.graversen.rust.rcon.events.PlayerConnectedEvent;
import io.graversen.rust.rcon.events.PlayerDeathEvent;
import io.graversen.rust.rcon.events.PlayerDisconnectedEvent;

import java.util.Arrays;
import java.util.Optional;

public class ConsoleToEventDigester
{
    public Optional<ConsoleDigests> digest(String consoleInput)
    {
        return Arrays.stream(ConsoleDigests.values()).filter(x -> x.matches(consoleInput)).findFirst();
    }

    public ChatMessageEvent digestChatMessageEvent()
    {
        return null;
    }

    public PlayerConnectedEvent digestPlayerConnectedEvent()
    {
        return null;
    }

    public PlayerDeathEvent digestPlayerDeathEvent()
    {
        return null;
    }

    public PlayerDisconnectedEvent digestPlayerDisconnectedEvent()
    {
        return null;
    }

    private enum ConsoleDigests implements IDigest
    {
        CHAT
                {
                    @Override
                    public boolean matches(String consoleInput)
                    {
                        return consoleInput.startsWith("[CHAT]");
                    }
                },
        PLAYER_CONNECTED
                {
                    @Override
                    public boolean matches(String consoleInput)
                    {
                        return consoleInput.contains("joined [");
                    }
                },
        PLAYER_DISCONNECTED
                {
                    @Override
                    public boolean matches(String consoleInput)
                    {
                        return consoleInput.contains("disconnecting:");
                    }
                },
        PLAYER_DEATH
                {
                    @Override
                    public boolean matches(String consoleInput)
                    {
                        return false;
                    }
                }
    }

    private interface IDigest
    {
        boolean matches(String consoleInput);
    }
}
