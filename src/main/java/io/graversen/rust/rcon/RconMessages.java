package io.graversen.rust.rcon;

public enum RconMessages implements IRconMessage
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
                    return consoleInput.contains("] was killed by ");
                }
            },
    PLAYER_SPAWNED
            {
                @Override
                public boolean matches(String consoleInput)
                {
                    return consoleInput.endsWith("has entered the game");
                }
            },
    WORLD_EVENT
            {
                @Override
                public boolean matches(String consoleInput)
                {
                    return consoleInput.startsWith("[event]");
                }
            },
    SAVE_EVENT
            {
                @Override
                public boolean matches(String consoleInput)
                {
                    return consoleInput.equalsIgnoreCase("Saving complete");
                }
            }
}
