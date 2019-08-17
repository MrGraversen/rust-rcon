package io.graversen.rust.rcon.support;

import io.graversen.rust.rcon.objects.RconReceive;
import io.graversen.rust.rcon.objects.rust.ISteamPlayer;
import io.graversen.rust.rcon.rustclient.IRconClient;
import io.graversen.trunk.io.serialization.interfaces.ISerializer;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ZLevelsSupport extends BaseModSupport
{
    private static final String JSON_PREFIX = "[ZLevelsRemastered] ";
    private final ISerializer serializer;

    public ZLevelsSupport(IRconClient rconClient, ISerializer serializer)
    {
        super(rconClient);
        this.serializer = serializer;
    }

    @Override
    public String modName()
    {
        return "ZLevels Remastered";
    }

    @Override
    public String description()
    {
        return "...";
    }

    @Override
    public String version()
    {
        return "2.9.5";
    }

    @Override
    public String umodLink()
    {
        return "https://umod.org/plugins/zlevels-remastered";
    }

    @Override
    public String umodDirectLink()
    {
        return "https://umod.org/plugins/ZLevelsRemastered.cs";
    }

    public PlayerInfo info(ISteamPlayer steamPlayer)
    {
        final String command = String.format("zl.json %s", steamPlayer.getSteamId());
        final RconReceive rconReceive = rconClient().sendAsyncBlocking(command, 1, TimeUnit.SECONDS);

        if (rconReceive != null)
        {
            final String json = rconReceive.getMessage().substring(JSON_PREFIX.length());
            final Map<String, String> data = serializer.deserialize(json, Map.class);

            final var skillsMap = data.entrySet().stream()
                    .map(levelEntry -> Map.entry(Skills.valueOf(levelEntry.getKey()), Integer.parseInt(levelEntry.getValue())))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            return new PlayerInfo(steamPlayer, skillsMap);
        }

        return null;
    }

    public void reset()
    {
        rconClient().send("zl.reset true");
    }

    public enum Skills
    {
        WOODCUTTING("WC"),
        MINING("M"),
        SKINNING("S"),
        ACQUIRE("A"),
        CRAFTING("C");

        private final String shortRepresentation;

        Skills(String shortRepresentation)
        {
            this.shortRepresentation = shortRepresentation;
        }

        public String getShortRepresentation()
        {
            return shortRepresentation;
        }
    }

    public class PlayerInfo
    {
        private final ISteamPlayer steamPlayer;
        private final Map<Skills, Integer> levels;

        PlayerInfo(ISteamPlayer steamPlayer, Map<Skills, Integer> levels)
        {
            this.steamPlayer = steamPlayer;
            this.levels = levels;
        }

        public ISteamPlayer getSteamPlayer()
        {
            return steamPlayer;
        }

        public Map<Skills, Integer> getLevels()
        {
            return levels;
        }
    }
}
