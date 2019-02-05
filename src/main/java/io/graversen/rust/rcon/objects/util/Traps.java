package io.graversen.rust.rcon.objects.util;

public enum Traps
{
    GUN_TRAP,
    AUTO_TURRET,
    FLAME_TURRET,
    LANDMINE,
    UNKNOWN;

    public static Traps parse(String string)
    {
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
