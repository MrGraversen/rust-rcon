package io.graversen.rust.rcon;

import io.graversen.rust.rcon.events.*;

import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConsoleToEventDigester
{
    private final Pattern squareBracketMatcher = Pattern.compile("\\[(.*?)]");

    public Optional<ConsoleDigests> digest(String consoleInput)
    {
        return Arrays.stream(ConsoleDigests.values()).filter(x -> x.matches(consoleInput)).findFirst();
    }

    public ChatMessageEvent digestChatMessageEvent(String consoleInput)
    {
        // [CHAT] Pope of the Nope[468295/76561197979952036] : ssss
        validateEvent(consoleInput, ConsoleDigests.CHAT);

        final String leftHandString = consoleInput.split(":")[0];
        final String chatMessage = consoleInput.split(":")[1].trim();

        final Matcher matcher = squareBracketMatcher.matcher(leftHandString);
        final String steamId64 = matcher.group(2).split("/")[1];

        final var chatMessageEvent = new ChatMessageEvent(, , chatMessage);

        return null;
    }

    public PlayerConnectedEvent digestPlayerConnectedEvent(String consoleInput)
    {
        // 82.102.20.179:52298/76561197979952036/Pope of the Nope joined [windows/76561197979952036]
        return null;
    }

    public PlayerDeathEvent digestPlayerDeathEvent(String consoleInput)
    {
        // Jiji[3831334/76561198189196679] was killed by [4267870/8141844]
        return null;
    }

    public PlayerKillEvent digestPlayerKillEvent(String consoleInput)
    {
        // [4267870/8141844] was killed by Jiji[3831334/76561198189196679]
        return null;
    }

    public PlayerDisconnectedEvent digestPlayerDisconnectedEvent(String consoleInput)
    {
        // 82.102.20.179:61714/76561197979952036/Pope of the Nope disconnecting: closing
        return null;
    }

    private void validateEvent(String consoleInput, ConsoleDigests consoleDigest)
    {
        this.digest(consoleInput)
                .map(c -> c.equals(consoleDigest))
                .orElseThrow(() -> new IllegalArgumentException("Invalid event type for console input"));
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
