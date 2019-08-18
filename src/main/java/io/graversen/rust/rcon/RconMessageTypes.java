package io.graversen.rust.rcon;

import io.graversen.rust.rcon.events.implementation.PlayerDeathEventParser;

import java.util.List;

public enum RconMessageTypes implements IRconMessage
{
    CHAT
            {
                @Override
                public boolean matches(String consoleInput)
                {
                    return consoleInput.startsWith("[CHAT] ");
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
                    return consoleInput.contains("disconnecting: ");
                }
            },
    PLAYER_DEATH
            {
                @Override
                public boolean matches(String consoleInput)
                {
                    return consoleInput.startsWith(PlayerDeathEventParser.MESSAGE_PREFIX);
                }
            },
    PLAYER_SPAWNED
            {
                @Override
                public boolean matches(String consoleInput)
                {
                    return consoleInput.endsWith("] has entered the game");
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
            },
    SUICIDE_EVENT
            {
                private List<String> validEndings = List.of(
                        "] was suicide by Suicide",
                        "] was suicide by Blunt",
                        "] was suicide by Stab",
                        "] was suicide by Explosion",
                        "] was killed by fall!",
                        "] was killed by Drowned",
                        "] was killed by Hunger",
                        "] was killed by Thirst",
                        "] was suicide by Heat",
                        "] was killed by Cold",
                        "] died (Fall)",
                        "] died (Bleeding)"
                );

                @Override
                public boolean matches(String consoleInput)
                {
                    return validEndings.stream().anyMatch(consoleInput::endsWith);
                }
            }
}
