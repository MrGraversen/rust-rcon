package io.graversen.rust.rcon;

import io.graversen.rust.rcon.listeners.IConsoleListener;
import io.graversen.rust.rcon.objects.rust.Player;

import java.io.Closeable;
import java.util.List;

public interface IRconClient extends Closeable
{
    List<Player> getCurrentPlayers();

    int sendRaw(String command);

    void attachConsoleListener(IConsoleListener consoleListener);

    Rcon rcon();
}