package io.graversen.rust.rcon.logging;

public class NoOpLogger implements ILogger
{
    @Override
    public void info(String message, Object... args)
    {
        // Shh...
    }

    @Override
    public void warning(String message, Object... args)
    {
        // Shh...
    }

    @Override
    public void error(String message, Object... args)
    {
        // Shh...
    }
}
