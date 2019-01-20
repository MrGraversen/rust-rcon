package io.graversen.rust.rcon.logging;

import java.io.PrintStream;

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

    @Override
    public PrintStream out()
    {
        return null;
    }

    @Override
    public PrintStream error()
    {
        return null;
    }
}
