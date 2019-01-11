package io.graversen.rust.rcon.events.types.console;

public class ConsoleMessageEvent extends BaseConsoleEvent
{
    private final String message;

    public ConsoleMessageEvent(String message)
    {
        this.message = message;
    }

    public String getMessage()
    {
        return message;
    }
}
