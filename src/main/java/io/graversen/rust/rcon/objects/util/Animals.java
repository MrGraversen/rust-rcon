package io.graversen.rust.rcon.objects.util;

public enum Animals
{
    BEAR,
    BOAR,
    CHICKEN,
    STAG,
    HORSE,
    WOLF,
    UNKNOWN;

    public static Animals parse(String string)
    {
        try
        {
            return Animals.valueOf(string.toUpperCase());
        }
        catch (IllegalArgumentException e)
        {
            return UNKNOWN;
        }
    }
}
