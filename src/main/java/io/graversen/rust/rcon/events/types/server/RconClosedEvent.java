package io.graversen.rust.rcon.events.types.server;

public class RconClosedEvent extends BaseServerEvent
{
    private final int code;
    private final String reason;

    public RconClosedEvent(int code, String reason)
    {
        this.code = code;
        this.reason = reason;
    }

    public int getCode()
    {
        return code;
    }

    public String getReason()
    {
        return reason;
    }
}
