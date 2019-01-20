package io.graversen.rust.rcon.logging;

public interface ILogger
{
    void info(String message, Object... args);

    void warning(String message, Object... args);

    void error(String message, Object... args);
}
