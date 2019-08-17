package io.graversen.rust.rcon.support;

import io.graversen.rust.rcon.rustclient.IRconClient;

public abstract class BaseModSupport
{
    private final IRconClient rconClient;

    protected BaseModSupport(IRconClient rconClient)
    {
        this.rconClient = rconClient;
    }

    protected IRconClient rconClient()
    {
        return rconClient;
    }

    public abstract String modName();

    public abstract String description();

    public abstract String version();

    public abstract String umodLink();

    public abstract String umodDirectLink();

    public abstract boolean requiresModification();
}
