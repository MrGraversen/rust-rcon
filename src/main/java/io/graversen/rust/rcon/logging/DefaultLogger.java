package io.graversen.rust.rcon.logging;

import io.graversen.rust.rcon.Constants;

import java.io.PrintStream;

public class DefaultLogger implements ILogger
{
    private static final String DEFAULT_TEMPLATE = "[%s]: %s";
    private static final String WARNING_TEMPLATE = String.format("[!] %s", DEFAULT_TEMPLATE);

    private final PrintStream out;
    private final PrintStream error;

    public DefaultLogger()
    {
        this.out = System.out;
        this.error = System.err;
    }

    public DefaultLogger(PrintStream out, PrintStream error)
    {
        this.out = out;
        this.error = error;
    }

    @Override
    public void info(String message, Object... args)
    {
        final String formattedMessage = String.format(message, args);
        out.println(String.format(DEFAULT_TEMPLATE, Constants.projectName(), formattedMessage));
    }

    @Override
    public void warning(String message, Object... args)
    {
        final String formattedMessage = String.format(message, args);
        out.println(String.format(WARNING_TEMPLATE, Constants.projectName(), formattedMessage));
    }

    @Override
    public void error(String message, Object... args)
    {
        final String formattedMessage = String.format(message, args);
        error.println(String.format(DEFAULT_TEMPLATE, Constants.projectName(), formattedMessage));
    }
}
