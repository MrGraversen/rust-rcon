package io.graversen.rust.rcon;

import java.util.Arrays;

public enum ConsoleDigests implements IDigest
{
    CHAT
            {
                @Override
                public boolean deepMatches(String consoleInput)
                {
                    return matches(consoleInput) && nothingElse(CHAT, consoleInput);
                }

                @Override
                public boolean matches(String consoleInput)
                {
                    return consoleInput.startsWith("[CHAT]");
                }
            },
    PLAYER_CONNECTED
            {
                @Override
                public boolean deepMatches(String consoleInput)
                {
                    return matches(consoleInput) && nothingElse(PLAYER_CONNECTED, consoleInput);
                }

                @Override
                public boolean matches(String consoleInput)
                {
                    return consoleInput.contains("joined [");
                }
            },
    PLAYER_DISCONNECTED
            {
                @Override
                public boolean deepMatches(String consoleInput)
                {
                    return matches(consoleInput) && nothingElse(PLAYER_DISCONNECTED, consoleInput);
                }

                @Override
                public boolean matches(String consoleInput)
                {
                    return consoleInput.contains("disconnecting:");
                }
            },
    PLAYER_DEATH
            {
                @Override
                public boolean deepMatches(String consoleInput)
                {
                    return matches(consoleInput) && nothingElse(PLAYER_DEATH, consoleInput);
                }

                @Override
                public boolean matches(String consoleInput)
                {
                    return consoleInput.contains("] was killed by [");
                }
            },
    SERVER_EVENT
            {
                @Override
                public boolean deepMatches(String consoleInput)
                {
                    return matches(consoleInput) && nothingElse(SERVER_EVENT, consoleInput);
                }

                @Override
                public boolean matches(String consoleInput)
                {
                    return consoleInput.startsWith("[event]");
                }
            };

    private static boolean nothingElse(ConsoleDigests except, String consoleInput)
    {
        return Arrays.stream(ConsoleDigests.values())
                .filter(c -> !c.equals(except))
                .noneMatch(c -> c.matches(consoleInput));
    }
}
