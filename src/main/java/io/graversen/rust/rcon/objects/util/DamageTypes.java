package io.graversen.rust.rcon.objects.util;

import java.util.Objects;

public enum DamageTypes
{
    BITE,
    BLUNT,
    BULLET,
    COLD,
    ELECTRIC,
    EXPLOSION,
    FALLING,
    RADIATION,
    SLASH,
    STAB,
    UNKNOWN;

    public static DamageTypes parse(String string)
    {
        if (Objects.isNull(string))
        {
            return UNKNOWN;
        }

        try
        {
            return DamageTypes.valueOf(string.toUpperCase());
        }
        catch (IllegalArgumentException e)
        {
            return UNKNOWN;
        }
    }

}
