package io.graversen.rust.rcon;

import io.graversen.rust.rcon.listeners.IConsoleListener;

import java.io.Closeable;

public interface IRconClient extends Closeable
{
    void sendRaw(String command);

    void attachConsoleListener(IConsoleListener consoleListener);
}