package io.graversen.rust.rcon.support;

import io.graversen.rust.rcon.IRconClient;

public abstract class BaseModSupport
{
    private final IRconClient rconClient;

    public BaseModSupport(IRconClient rconClient)
    {
        this.rconClient = rconClient;
    }

    public abstract String modName();

    public abstract String description();

    public abstract String version();
}
