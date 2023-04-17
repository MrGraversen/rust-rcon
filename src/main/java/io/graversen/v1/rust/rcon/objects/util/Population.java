package io.graversen.v1.rust.rcon.objects.util;

public class Population
{
    private final int current;
    private final int max;

    public Population(int current, int max)
    {
        this.current = current;
        this.max = max;
    }

    public int getCurrent()
    {
        return current;
    }

    public int getMax()
    {
        return max;
    }

    public boolean extinct()
    {
        return current == 0;
    }

    public boolean full()
    {
        return current == max;
    }
}
