package io.graversen.v1.rust.rcon.logging;

import java.io.PrintStream;

public class NoOpLogger implements ILogger
{
    @Override
    public void debug(String message, Object... args)
    {
        // Shh...
    }

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
    public void logLevelEnabled(LogLevels logLevel, boolean enabled)
    {
        // Nothing to see here
    }

    @Override
    public boolean isLogLevelEnabled(LogLevels logLevel)
    {
        return false;
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
