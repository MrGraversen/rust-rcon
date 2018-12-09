package io.graversen.rust.rcon.util;

import io.graversen.rust.rcon.IRconClient;
import io.graversen.rust.rcon.composite.Loadout;

public abstract class RconUtils
{
    public void executeLoadout(IRconClient rconClient, Loadout loadout)
    {
        loadout.forEach(
                item -> rconClient.rcon().inventory().giveTo(loadout.getPlayerName(), item.getShortName(), item.getAmount())
        );
    }
}
