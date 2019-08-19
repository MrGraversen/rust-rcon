package io.graversen.rust.rcon.support.internal.thejudge;

import io.graversen.rust.rcon.objects.rust.ISteamPlayer;
import io.graversen.rust.rcon.rustclient.IRconClient;
import io.graversen.rust.rcon.support.BaseModSupport;

public class TheJudge extends BaseModSupport
{
    public TheJudge(IRconClient rconClient)
    {
        super(rconClient);
    }

    @Override
    public String modName()
    {
        return "TheJudge (Ownzone)";
    }

    @Override
    public String description()
    {
        return "Internally developed mod to dispense justice in lawless land of Rust";
    }

    @Override
    public String version()
    {
        return "0.0.1";
    }

    @Override
    public String umodLink()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public String umodDirectLink()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean requiresModification()
    {
        return false;
    }

    public void hurt(ISteamPlayer steamPlayer, int amount)
    {
        if (amount > 0)
        {
            rconClient().send(String.format("judge.hurt %s %s", steamPlayer.getSteamId(), amount));
        }
    }

    public void bleed(ISteamPlayer steamPlayer, int amount)
    {
        if (amount > 0)
        {
            rconClient().send(String.format("judge.bleed %s %s", steamPlayer.getSteamId(), amount));
        }
    }

    public void starve(ISteamPlayer steamPlayer)
    {
        rconClient().send(String.format("judge.starve %s", steamPlayer.getSteamId()));
    }

    public void suffocate(ISteamPlayer steamPlayer)
    {
        rconClient().send(String.format("judge.suffocate %s", steamPlayer.getSteamId()));
    }

    public void cook(ISteamPlayer steamPlayer)
    {
        rconClient().send(String.format("judge.cook %s", steamPlayer.getSteamId()));
    }

    public void freeze(ISteamPlayer steamPlayer)
    {
        rconClient().send(String.format("judge.freeze %s", steamPlayer.getSteamId()));
    }

    public void poison(ISteamPlayer steamPlayer, int amount)
    {
        if (amount > 0)
        {
            rconClient().send(String.format("judge.poison %s %s", steamPlayer.getSteamId(), amount));
        }
    }

    public void radiation(ISteamPlayer steamPlayer, int amount)
    {
        if (amount > 0)
        {
            rconClient().send(String.format("judge.radiation %s %s", steamPlayer.getSteamId(), amount));
        }
    }

    public void pardon(ISteamPlayer steamPlayer)
    {
        rconClient().send(String.format("judge.pardon %s", steamPlayer.getSteamId()));
    }
}
