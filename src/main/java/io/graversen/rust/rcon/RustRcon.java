package io.graversen.rust.rcon;

import io.graversen.rust.rcon.listeners.IConsoleListener;
import io.graversen.rust.rcon.objects.rust.Player;

import java.util.List;

public class RustRcon
{
    public static void main(String[] args)
    {
        final IRconClient rconClient = RconClient.connect("graversen.io", Constants.RCON_PASSWORD, 30204);
        rconClient.attachConsoleListener(consoleListener());
        List<Player> currentPlayers = rconClient.getCurrentPlayers();
    }

    private static IConsoleListener consoleListener()
    {
        return System.out::println;
    }
}
