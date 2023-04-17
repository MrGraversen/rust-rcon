package io.graversen.v1.rust.rcon.events.types.server;

public class RconErrorEvent extends BaseServerEvent
{
    private final Exception exception;

    public RconErrorEvent(Exception exception)
    {
        this.exception = exception;
    }

    public Exception getException()
    {
        return exception;
    }
}
