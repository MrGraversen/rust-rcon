package io.graversen.v1.rust.rcon.logging;

public enum LogLevels
{
    DEBUG(false),
    INFO(true),
    WARNING(true),
    ERROR(true);

    private final boolean defaultState;

    LogLevels(boolean defaultState)
    {
        this.defaultState = defaultState;
    }

    public boolean defaultState()
    {
        return defaultState;
    }
}
