package io.graversen.rust.rcon.rustclient;

import io.graversen.rust.rcon.objects.RconReceive;

import java.util.concurrent.CompletableFuture;

public interface IRconClient
{
    void send(String rconMessage);

    CompletableFuture<RconReceive> sendAsync(String rconMessage);
}
