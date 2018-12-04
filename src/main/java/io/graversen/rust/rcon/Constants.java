package io.graversen.rust.rcon;

import io.graversen.trunk.io.IOUtils;

public class Constants
{
    private static final IOUtils ioUtils = IOUtils.automaticProjectName();

    private Constants()
    {

    }

    public static String rconPassword()
    {
        return ioUtils
                .readResource("rcon_password.txt")
                .toString()
                .trim();
    }
}
