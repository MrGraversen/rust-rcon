package io.graversen.v1.rust.rcon.objects.util;

import java.util.Objects;

public enum Traps
{
    GUN_TRAP,
    AUTO_TURRET,
    FLAME_TURRET,
    LANDMINE,
    UNKNOWN;

    public static Traps parse(String string)
    {
        if (Objects.isNull(string))
        {
            return UNKNOWN;
        }

        try
        {
            return Traps.valueOf(string.toUpperCase().replaceAll("\\s", "_"));
        }
        catch (IllegalArgumentException e)
        {
            return UNKNOWN;
        }
    }
}
