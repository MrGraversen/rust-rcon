package io.graversen.rust.rcon.logging;

import java.io.PrintStream;

public interface ILogger
{
    void debug(String message, Object... args);

    void info(String message, Object... args);

    void warning(String message, Object... args);

    void error(String message, Object... args);

    void logLevelEnabled(LogLevels logLevel, boolean enabled);

    PrintStream out();

    PrintStream error();
}
