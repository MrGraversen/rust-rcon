package io.graversen.rust.rcon.protocol;

import io.graversen.rust.rcon.rustclient.IRconClient;

import java.util.Arrays;
import java.util.Objects;

class BaseRcon
{
    private final IRconClient rconClient;

    protected BaseRcon(IRconClient rconClient)
    {
        this.rconClient = rconClient;
    }

    protected IRconClient rconClient()
    {
        return rconClient;
    }

    protected RconEntity rconEntity(String rconMessage, Object... args)
    {
        if (Arrays.stream(args).anyMatch(Objects::isNull)) throw new IllegalArgumentException("Cannot construct RCON messages with null-values");
        return new RconEntity(String.format(rconMessage, args), rconClient());
    }
}
