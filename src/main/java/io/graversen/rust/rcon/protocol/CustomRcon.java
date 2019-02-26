package io.graversen.rust.rcon.protocol;

import io.graversen.rust.rcon.objects.rust.ISteamPlayer;
import io.graversen.rust.rcon.rustclient.IRconClient;

public class CustomRcon extends BaseRcon
{
    CustomRcon(IRconClient rconClient)
    {
        super(rconClient);
    }

    public RconEntity broadcast(String message)
    {
        return rconEntity("broadcast \"%s\"", message);
    }

    public RconEntity broadcastTo(ISteamPlayer steamPlayer, String message)
    {
        return rconEntity("broadcastto \"%s\" \"%s\"", steamPlayer.getSteamId(), message);
    }
}
