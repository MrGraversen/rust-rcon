package io.graversen.rust.rcon.logging;

import java.io.PrintStream;

public class DefaultLogger implements ILogger
{
    private static final String DEFAULT_TEMPLATE = "[%s]: %s";
    private static final String WARNING_TEMPLATE = String.format("[!] %s", DEFAULT_TEMPLATE);

    private final Class ofClass;

    private final PrintStream out;
    private final PrintStream error;

    public DefaultLogger(Class ofClass)
    {
        this.ofClass = ofClass;
        this.out = System.out;
        this.error = System.err;
    }

    public DefaultLogger(Class ofClass, PrintStream out, PrintStream error)
    {
        this.ofClass = ofClass;
        this.out = out;
        this.error = error;
    }

    @Override
    public void info(String message, Object... args)
    {
        final String formattedMessage = String.format(message, args);
        out().println(String.format(DEFAULT_TEMPLATE, ofClass.getSimpleName(), formattedMessage));
    }

    @Override
    public void warning(String message, Object... args)
    {
        final String formattedMessage = String.format(message, args);
        out().println(String.format(WARNING_TEMPLATE, ofClass.getSimpleName(), formattedMessage));
    }

    @Override
    public void error(String message, Object... args)
    {
        final String formattedMessage = String.format(message, args);
        error().println(String.format(DEFAULT_TEMPLATE, ofClass.getSimpleName(), formattedMessage));
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
