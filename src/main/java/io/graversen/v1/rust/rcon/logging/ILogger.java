package io.graversen.v1.rust.rcon.logging;

import java.io.PrintStream;

public interface ILogger
{
    void debug(String message, Object... args);

    void info(String message, Object... args);

    void warning(String message, Object... args);

    void error(String message, Object... args);

    void logLevelEnabled(LogLevels logLevel, boolean enabled);

    boolean isLogLevelEnabled(LogLevels logLevel);

    PrintStream out();

    PrintStream error();
}
