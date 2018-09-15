package io.graversen.rust.rcon;

import io.graversen.rust.rcon.composite.Loadout;

public abstract class RconUtils
{
    public void executeLoadout(IRconClient rconClient, Loadout loadout)
    {
        loadout.getItems().forEach(
                item -> rconClient.rcon().inventory().giveTo(loadout.getPlayerName(), item.getShortName(), item.getAmount())
        );
    }
}
