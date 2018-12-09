package io.graversen.rust.rcon.support;

import io.graversen.rust.rcon.IRconClient;
import io.graversen.rust.rcon.objects.rust.Player;

public class AirstrikeMod extends BaseModSupport
{
    private final String BASE_COMMAND = "airstrike";

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
        rconClient().sendRaw(command);
    }

    public void callStrike(StrikeTypes strikeType, String player)
    {
        final String command = String.format("%s %s %s", BASE_COMMAND, strikeType.name().toLowerCase(), player);
        rconClient().sendRaw(command);
    }

    public void callStrike(StrikeTypes strikeType, Player player)
    {
        final String command = String.format("%s %s %s", BASE_COMMAND, strikeType.name().toLowerCase(), player.getDisplayName());
        rconClient().sendRaw(command);
    }

    public enum StrikeTypes
    {
        STRIKE,
        SQUAD
    }
}
