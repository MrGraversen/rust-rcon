package io.graversen.rust.rcon.protocol;

import io.graversen.rust.rcon.objects.RconReceive;
import io.graversen.rust.rcon.objects.rust.*;
import io.graversen.rust.rcon.objects.util.Population;
import io.graversen.rust.rcon.rustclient.IRconClient;
import io.graversen.trunk.io.serialization.interfaces.ISerializer;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class InfoRcon extends BaseRcon
{
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(1);

    private static final String SPAWN_REPORT_KEY_SEPARATOR = "\n\t";
    private static final String SPAWN_REPORT_VALUE_SEPARATOR = "\n\n";
    private static final int SPAWN_REPORT_POPULATION_READ_AHEAD = "Population: ".length();

    private final ISerializer serializer;

    InfoRcon(IRconClient rconClient, ISerializer serializer)
    {
        super(rconClient);
        this.serializer = serializer;
    }

    public List<Player> getCurrentPlayers()
    {
        final List<Player> playerList = new ArrayList<>();
        final RconReceive playerListRcon = rconClient().sendAsyncBlocking("playerlist", DEFAULT_TIMEOUT.getSeconds(), TimeUnit.SECONDS);

        if (Objects.nonNull(playerListRcon))
        {
            try
            {
                final Player[] players = serializer.deserialize(playerListRcon.getMessage(), Player[].class);
                playerList.addAll(Arrays.asList(players));
            }
            catch (Exception e)
            {
                // ¯\_(ツ)_/¯
            }
        }

        return playerList;
    }

    public BuildInfo getBuildInfo()
    {
        final RconReceive buildInfoRcon = rconClient().sendAsyncBlocking("global.buildinfo", DEFAULT_TIMEOUT.getSeconds(), TimeUnit.SECONDS);

        try
        {
            return serializer.deserialize(buildInfoRcon.getMessage(), BuildInfo.class);
        }
        catch (Exception e)
        {
            // ¯\_(ツ)_/¯
        }

        return null;
    }

    public List<BanInfo> getBanInfo()
    {
        final RconReceive banInfoRcon = rconClient().sendAsyncBlocking("global.bans", DEFAULT_TIMEOUT.getSeconds(), TimeUnit.SECONDS);

        try
        {
            final BanInfo[] banInfos = serializer.deserialize(banInfoRcon.getMessage(), BanInfo[].class);
            return List.of(banInfos);
        }
        catch (Exception e)
        {
            // ¯\_(ツ)_/¯
        }

        return null;
    }

    public ServerInfo getServerInfo()
    {
        final RconReceive serverInfoRcon = rconClient().sendAsyncBlocking("global.serverinfo", DEFAULT_TIMEOUT.getSeconds(), TimeUnit.SECONDS);

        try
        {
            return serializer.deserialize(serverInfoRcon.getMessage(), ServerInfo.class);
        }
        catch (Exception e)
        {
            // ¯\_(ツ)_/¯
        }

        return null;
    }

    public SpawnReport getSpawnReport()
    {
        final RconReceive spawnReport = rconClient().sendAsyncBlocking("spawn.report", DEFAULT_TIMEOUT.getSeconds(), TimeUnit.SECONDS);
        final String message = spawnReport.getMessage();

        final Map<String, Population> populationMap = new HashMap<>();

        int needle = 0;
        while (needle < message.length() - SPAWN_REPORT_VALUE_SEPARATOR.length())
        {
            final int keyNeedle = message.indexOf(SPAWN_REPORT_KEY_SEPARATOR, needle);
            final int keyReadAhead = needle == 0 ? needle : needle + SPAWN_REPORT_KEY_SEPARATOR.length();
            final String key = message.substring(keyReadAhead, keyNeedle);

            final int valueNeedle = message.indexOf(SPAWN_REPORT_VALUE_SEPARATOR, keyNeedle);
            final String value = message.substring(keyNeedle + SPAWN_REPORT_VALUE_SEPARATOR.length(), valueNeedle);
            final String valueTrimmed = value.substring(SPAWN_REPORT_POPULATION_READ_AHEAD);
            final String[] populationValues = valueTrimmed.split("/");
            final var population = new Population(Integer.parseInt(populationValues[0]), Integer.parseInt(populationValues[1]));

            populationMap.put(key, population);

            needle = valueNeedle;
        }

        return new SpawnReport(populationMap);
    }
}
