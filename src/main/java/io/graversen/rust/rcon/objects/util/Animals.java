package io.graversen.rust.rcon.objects.util;

import java.util.Objects;

public enum Animals
{
    BEAR(2),
    BOAR(5),
    CHICKEN(3),
    STAG(3),
    RIDABLEHORSE(1),
    WOLF(2),
    UNKNOWN(0);

    private final int defaultPopulation;

    Animals(int defaultPopulation)
    {
        this.defaultPopulation = defaultPopulation;
    }

    public int defaultPopulation()
    {
        return defaultPopulation;
    }

    public static Animals parse(String string)
    {
        if (Objects.isNull(string))
        {
            return UNKNOWN;
        }

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
