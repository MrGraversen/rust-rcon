package io.graversen.rust.rcon.rustclient;

import io.graversen.rust.rcon.objects.RconReceive;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public interface IRconClient
{
    void send(String rconMessage);

    CompletableFuture<RconReceive> sendAsync(String rconMessage);

    RconReceive sendAsyncBlocking(String rconMessage);

    RconReceive sendAsyncBlocking(String rconMessage, long timeout, TimeUnit timeUnit);
}
