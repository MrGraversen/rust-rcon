package io.graversen.rust.rcon.protocol;

import io.graversen.rust.rcon.rustclient.IRconClient;

public class BaseRcon
{
    private final IRconClient rconClient;

    public BaseRcon(IRconClient rconClient)
    {
        this.rconClient = rconClient;
    }

    protected IRconClient rconClient()
    {
        return rconClient;
    }

    protected RconEntity rconEntity(String rconMessage, Object... args)
    {
        return new RconEntity(String.format(rconMessage, args), rconClient());
    }
}
