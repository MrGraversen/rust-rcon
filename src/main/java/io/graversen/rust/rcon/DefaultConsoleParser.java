package io.graversen.rust.rcon;

import io.graversen.rust.rcon.events.*;
import io.graversen.rust.rcon.util.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DefaultConsoleParser
{
    private final Pattern squareBracketInsideMatcher = Pattern.compile("\\[(.*?)\\]");
    private final Pattern squareBracketOutsideMatcher = Pattern.compile("\\](.*?)\\[");

    public Optional<RconMessages> parse(String consoleInput)
    {
        return Arrays.stream(RconMessages.values())
                .filter(rconMessage -> deepMatches(consoleInput, rconMessage))
                .findFirst();
    }

    public SaveEvent parseSaveEvent(String consoleInput)
    {
        return new SaveEvent();
    }

    public ChatMessageEvent parseChatMessageEvent(String consoleInput)
    {
        validateEvent(consoleInput, RconMessages.CHAT);

        final String[] chatMessageParts = consoleInput.split("\\s:\\s", 2);

        final String leftHandString = chatMessageParts[0];
        final String chatMessage = chatMessageParts[1].trim();

        final Matcher matcherSteamId = squareBracketInsideMatcher.matcher(leftHandString);

        List<String> matchingStrings = new ArrayList<>();
        while (matcherSteamId.find())
        {
            matchingStrings.add(matcherSteamId.group(1));
        }

        final String steamId64 = matchingStrings.get(matchingStrings.size() - 1).split("/")[1];

        final Matcher matcherPlayerName = squareBracketOutsideMatcher.matcher(leftHandString);

        String playerName = "N/A";
        if (matcherPlayerName.find())
        {
            playerName = matcherPlayerName.group(1).trim();
        }

        return new ChatMessageEvent(playerName, steamId64, chatMessage);
    }

    public PlayerConnectedEvent parsePlayerConnectedEvent(String consoleInput)
    {
        validateEvent(consoleInput, RconMessages.PLAYER_CONNECTED);

        final String[] splitInput = consoleInput.split("/");

        final String connectionTuple = splitInput[0];
        final String steamId64 = splitInput[1];

        final int osDescriptorFromIndex = consoleInput.lastIndexOf('[') + 1;
        final int osDescriptorToIndex = consoleInput.lastIndexOf('/');
        final String osDescriptor = consoleInput.substring(osDescriptorFromIndex, osDescriptorToIndex);

        final int playerNameFromIndex = Utils.nthIndexOf(consoleInput, '/', 2) + 1;
        final int playerNameToIndex = consoleInput.lastIndexOf("joined");
        final String playerName = consoleInput.substring(playerNameFromIndex, playerNameToIndex).trim();

        return new PlayerConnectedEvent(connectionTuple, osDescriptor, steamId64, playerName);
    }

    public PlayerDeathEvent parsePlayerDeathEvent(String consoleInput)
    {
        // [4267870/8141844] was killed by Jiji[3831334/76561198189196679]
        // UnknOwn  csgolive.com[9460039/76561198104761939] was killed by Doctor Delete[9126388/76561197979952036]
        // -_-[9468122/76561198165597845] was killed by Doctor Delete[9126388/76561197979952036]
        // Jiji[3831334/76561198189196679] was killed by [4267870/8141844]
        // TheLyss Gamer[4147282/76561198154440141] was killed by boar (Boar)
        // TheLyss Gamer[4147282/76561198154440141] was killed by Doctor Delete[168316/76561197979952036]


        throw new UnsupportedOperationException();
    }

    public PlayerDisconnectedEvent parsePlayerDisconnectedEvent(String consoleInput)
    {
        validateEvent(consoleInput, RconMessages.PLAYER_DISCONNECTED);

        final String[] splitInput = consoleInput.split("/");
        final String[] splitInputLastElement = splitInput[2].split("disconnecting:");

        final String ipAddress = splitInput[0];
        final String steamId64 = splitInput[1];
        final String playerName = splitInputLastElement[0].trim();
        final String reason = splitInputLastElement[1].trim();

        return new PlayerDisconnectedEvent(ipAddress, steamId64, playerName, reason);
    }

    public PlayerSpawnedEvent parserPlayerSpawnedEvent(String consoleInput)
    {
        validateEvent(consoleInput, RconMessages.PLAYER_SPAWNED);

        final Matcher matcherSteamId = squareBracketInsideMatcher.matcher(consoleInput);

        List<String> matchingStrings = new ArrayList<>();
        while (matcherSteamId.find())
        {
            matchingStrings.add(matcherSteamId.group(1));
        }

        final String steamId64 = matchingStrings.get(matchingStrings.size() - 1).split("/")[1];
        final String playerName = consoleInput.split("\\[")[0].trim();

        return new PlayerSpawnedEvent(playerName, steamId64);
    }

    public WorldEvent parseWorldEvent(String consoleInput)
    {
        if (consoleInput.equalsIgnoreCase("[event] assets/prefabs/npc/cargo plane/cargo_plane.prefab"))
        {
            return new WorldEvent(WorldEvent.EventTypes.CARGO_PLANE);
        }
        else if (consoleInput.equalsIgnoreCase("[event] assets/prefabs/npc/ch47/ch47scientists.entity.prefab"))
        {
            return new WorldEvent(WorldEvent.EventTypes.CH47_SCIENTISTS);
        }
        else if (consoleInput.equalsIgnoreCase("[event] assets/prefabs/npc/patrol helicopter/patrolhelicopter.prefab"))
        {
            return new WorldEvent(WorldEvent.EventTypes.PATROL_HELICOPTER);
        }
        else
        {
            return new WorldEvent(WorldEvent.EventTypes.UNKNOWN);
        }
    }

    public void validateEvent(String consoleInput, RconMessages consoleDigest)
    {
        parse(consoleInput)
                .filter(c -> c.equals(consoleDigest))
                .orElseThrow(() -> new IllegalArgumentException("Invalid event type for console input"));
    }

    private boolean deepMatches(String consoleInput, RconMessages rconMessage)
    {
        return rconMessage.matches(consoleInput) && nothingElse(rconMessage, consoleInput);
    }

    private static boolean nothingElse(RconMessages except, String consoleInput)
    {
        return Arrays.stream(RconMessages.values())
                .filter(c -> !c.equals(except))
                .noneMatch(c -> c.matches(consoleInput));
    }
}
