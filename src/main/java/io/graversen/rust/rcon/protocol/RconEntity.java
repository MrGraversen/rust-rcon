package io.graversen.rust.rcon.protocol;

import io.graversen.rust.rcon.objects.RconReceive;
import io.graversen.rust.rcon.rustclient.IRconClient;

import java.util.concurrent.CompletableFuture;

public class RconEntity
{
    private final String rconMessage;
    private final IRconClient rconClient;

    public RconEntity(String rconMessage, IRconClient rconClient)
    {
        this.rconMessage = rconMessage;
        this.rconClient = rconClient;
    }

    public String getRconMessage()
    {
        return rconMessage;
    }

    public void execute()
    {
        rconClient.send(getRconMessage());
    }

    public CompletableFuture<RconReceive> executeAsync()
    {
        return rconClient.sendAsync(getRconMessage());
    }
}
