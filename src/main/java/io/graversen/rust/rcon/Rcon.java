package io.graversen.rust.rcon;

public class Rcon
{
    private final static int DEFAULT_TIMEOUT = 5000;
    private final IRconClient rconClient;

    public Rcon(IRconClient rconClient)
    {
        this.rconClient = rconClient;
    }
}
