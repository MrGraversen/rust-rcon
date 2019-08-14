package io.graversen.rust.rcon.support;

import io.graversen.rust.rcon.objects.rust.Player;
import io.graversen.rust.rcon.rustclient.IRconClient;

public class AirstrikeMod extends BaseModSupport implements IUmodPermissible
{
    private static final String BASE_COMMAND = "airstrike";

    public AirstrikeMod(IRconClient rconClient)
    {
        super(rconClient);
    }

    @Override
    public String modName()
    {
        return "Airstrike";
    }

    @Override
    public String description()
    {
        return "Call an airstrike";
    }

    @Override
    public String version()
    {
        return "v0.3.5";
    }

    @Override
    public String umodLink()
    {
        return "https://umod.org/plugins/airstrike";
    }

    @Override
    public String umodDirectLink()
    {
        return "https://umod.org/plugins/Airstrike.cs";
    }

    public void callStrikeRandom(StrikeTypes strikeType)
    {
        final String command = String.format("%s %s random", BASE_COMMAND, strikeType.name().toLowerCase());
        rconClient().send(command);
    }

    public void callStrike(StrikeTypes strikeType, String player)
    {
        final String command = String.format("%s %s \"%s\"", BASE_COMMAND, strikeType.name().toLowerCase(), player);
        rconClient().send(command);
    }

    public void callStrike(StrikeTypes strikeType, Player player)
    {
        final String command = String.format("%s %s \"%s\"", BASE_COMMAND, strikeType.name().toLowerCase(), player.getDisplayName());
        rconClient().send(command);
    }

    @Override
    public String baseName()
    {
        return "airstrike";
    }

    public enum StrikeTypes
    {
        STRIKE,
        SQUAD
    }
}
