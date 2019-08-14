package io.graversen.rust.rcon.objects.util;

public enum Vehicles
{
    MINICOPTER(1),
    MOTORROWBOAT(4),
    RHIB(1);

    private final int defaultPopulation;

    Vehicles(int defaultPopulation)
    {
        this.defaultPopulation = defaultPopulation;
    }

    public int defaultPopulation()
    {
        return defaultPopulation;
    }
}
