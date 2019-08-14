package io.graversen.rust.rcon.protocol;

import io.graversen.rust.rcon.objects.RconReceive;
import io.graversen.rust.rcon.objects.rust.BanInfo;
import io.graversen.rust.rcon.objects.rust.BuildInfo;
import io.graversen.rust.rcon.objects.rust.Player;
import io.graversen.rust.rcon.objects.rust.ServerInfo;
import io.graversen.rust.rcon.rustclient.IRconClient;
import io.graversen.trunk.io.serialization.interfaces.ISerializer;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class InfoRcon extends BaseRcon
{
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(1);
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
}
