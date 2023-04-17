package io.graversen.v1.rust.rcon.protocol;

import io.graversen.v1.rust.rcon.rustclient.IRconClient;

import java.util.Arrays;
import java.util.Objects;

abstract class BaseRcon
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
