package io.graversen.rust.rcon;

import java.io.Closeable;
import java.util.concurrent.Future;

public interface IRconClient extends Closeable
{
    void sendRaw(String command);
}