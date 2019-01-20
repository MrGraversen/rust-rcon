package io.graversen.rust.rcon.events.implementation;

import io.graversen.rust.rcon.events.IEventParser;
import io.graversen.rust.rcon.events.types.player.ChatMessageEvent;
import io.graversen.rust.rcon.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;

public class ChatMessageEventParser implements IEventParser<ChatMessageEvent>
{
    @Override
    public Function<String, Optional<ChatMessageEvent>> parseEvent()
    {
        return rconMessage ->
        {
            final String[] chatMessageParts = rconMessage.split("\\s:\\s", 2);

            final String leftHandString = chatMessageParts[0];
            final String chatMessage = chatMessageParts[1].trim();

            final Matcher matcherSteamId = Utils.squareBracketInsideMatcher.matcher(leftHandString);

            List<String> matchingStrings = new ArrayList<>();
            while (matcherSteamId.find())
            {
                matchingStrings.add(matcherSteamId.group(1));
            }

            final String steamId64 = matchingStrings.get(matchingStrings.size() - 1).split("/")[1];

            final Matcher matcherPlayerName = Utils.squareBracketOutsideMatcher.matcher(leftHandString);

            String playerName = "N/A";
            if (matcherPlayerName.find())
            {
                playerName = matcherPlayerName.group(1).trim();
            }

            return Optional.of(new ChatMessageEvent(playerName, steamId64, chatMessage));
        };
    }
}
