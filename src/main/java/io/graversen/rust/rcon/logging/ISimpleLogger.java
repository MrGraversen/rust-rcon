package io.graversen.rust.rcon.logging;

@FunctionalInterface
public interface ISimpleLogger extends ILogger
{
    @Override
    void info(String message, Object... args);

    @Override
    default void warning(String message, Object... args)
    {
        info(message, args);
    }

    @Override
    default void error(String message, Object... args)
    {
        info(message, args);
    }
}
