package io.graversen.rust.rcon.logging;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultLogger implements ILogger
{
    private static final String DEFAULT_TEMPLATE = "[%s]: %s";
    private static final String WARNING_TEMPLATE = String.format("[!] %s", DEFAULT_TEMPLATE);

    private final Class ofClass;

    private final PrintStream out;
    private final PrintStream error;

    private final Map<LogLevels, Boolean> logLevelsEnabled;

    public DefaultLogger(Class ofClass)
    {
        this(ofClass, System.out, System.err);
    }

    public DefaultLogger(Class ofClass, PrintStream out, PrintStream error)
    {
        this.ofClass = ofClass;
        this.out = out;
        this.error = error;

        this.logLevelsEnabled = new ConcurrentHashMap<>();
        Arrays.stream(LogLevels.values()).forEach(level -> this.logLevelsEnabled.put(level, true));
    }

    @Override
    public void debug(String message, Object... args)
    {
        if (logLevelsEnabled.getOrDefault(LogLevels.DEBUG, false))
        {
            final String formattedMessage = String.format(message, args);
            out().println(String.format(DEFAULT_TEMPLATE, ofClass.getSimpleName(), formattedMessage));
        }
    }

    @Override
    public void info(String message, Object... args)
    {
        if (logLevelsEnabled.getOrDefault(LogLevels.INFO, false))
        {
            final String formattedMessage = String.format(message, args);
            out().println(String.format(DEFAULT_TEMPLATE, ofClass.getSimpleName(), formattedMessage));
        }
    }

    @Override
    public void warning(String message, Object... args)
    {
        if (logLevelsEnabled.getOrDefault(LogLevels.WARNING, false))
        {
            final String formattedMessage = String.format(message, args);
            out().println(String.format(WARNING_TEMPLATE, ofClass.getSimpleName(), formattedMessage));
        }
    }

    @Override
    public void error(String message, Object... args)
    {
        if (logLevelsEnabled.getOrDefault(LogLevels.ERROR, false))
        {
            final String formattedMessage = String.format(message, args);
            error().println(String.format(WARNING_TEMPLATE, ofClass.getSimpleName(), formattedMessage));
        }
    }

    @Override
    public void logLevelEnabled(LogLevels logLevel, boolean enabled)
    {
        logLevelsEnabled.put(logLevel, enabled);
    }

    @Override
    public PrintStream out()
    {
        return out;
    }

    @Override
    public PrintStream error()
    {
        return error;
    }
}
