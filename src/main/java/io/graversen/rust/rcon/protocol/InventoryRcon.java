package io.graversen.rust.rcon.protocol;

import io.graversen.rust.rcon.objects.rust.ISteamPlayer;
import io.graversen.rust.rcon.rustclient.IRconClient;

public class InventoryRcon extends BaseRcon
{
    InventoryRcon(IRconClient rconClient)
    {
        super(rconClient);
    }

    public RconEntity giveTo(ISteamPlayer steamPlayer, String itemShortName, int amount)
    {
        return rconEntity(String.format("inventory.giveto \"%s\" \"%s\" \"%d\"", steamPlayer.getSteamId(), itemShortName, amount));
    }

    public RconEntity giveArm(ISteamPlayer steamPlayer, String itemShortName, int amount)
    {
        return rconEntity(String.format("inventory.givearm \"%s\" \"%s\" \"%d\"", steamPlayer.getSteamId(), itemShortName, amount));
    }

    public RconEntity giveAll(String itemShortName, int amount)
    {
        return rconEntity(String.format("inventory.giveall \"%s\" \"%d\"", itemShortName, amount));
    }
}
