package io.graversen.rust.rcon.support.internal.broadcast;

import io.graversen.rust.rcon.objects.rust.ISteamPlayer;
import io.graversen.rust.rcon.rustclient.IRconClient;
import io.graversen.rust.rcon.support.BaseModSupport;

public class BroadcastMod extends BaseModSupport
{
    private static final String BROADCAST_ALL_COMMAND = "broadcast_all";
    private static final String BROADCAST_TO_COMMAND = "broadcast_to";

    public BroadcastMod(IRconClient rconClient)
    {
        super(rconClient);
    }

    @Override
    public String modName()
    {
        return "Broadcast (Ownzone)";
    }

    @Override
    public String description()
    {
        return "Internally developed mod to aid sending messages to all or a single player";
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

    public void broadcastAll(IMessage message)
    {

    }

    public void broadcastTo(IMessage message, ISteamPlayer steamPlayer)
    {

    }
}
