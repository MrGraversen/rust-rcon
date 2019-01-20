package io.graversen.rust.rcon;

import io.graversen.rust.rcon.logging.ILogger;
import io.graversen.rust.rcon.logging.NoOpLogger;
import io.graversen.trunk.io.IOUtils;

public class Constants
{
    private static final IOUtils ioUtils = new IOUtils(".rustrcon");
    private static final ILogger noOpLogger = new NoOpLogger();

    private Constants()
    {

    }

    public static String projectName()
    {
        return "Rust RCON";
    }

    public static String rconPassword()
    {
        return ioUtils
                .readResource("rcon_password.txt")
                .toString()
                .trim();
    }

    public static ILogger noOpLogger()
    {
        return noOpLogger;
    }
}
