package io.graversen.rust.rcon;

import io.graversen.rust.rcon.listeners.IConsoleListener;
import io.graversen.rust.rcon.objects.rust.Player;

import java.util.List;

public class RustRcon
{
    public static void main(String[] args) throws RconException
    {
        final IRconClient rconClient = RconClient.connect("graversen.io", Constants.rconPassword(), 30204);
        rconClient.attachConsoleListener(consoleListener());
        List<Player> currentPlayers = rconClient.getCurrentPlayers();
        System.out.printf("Current Player Count: %d\n", currentPlayers.size());
//
//        rconClient.rcon().inventory().giveTo("doctor delete", "stones", 100000000);
//        rconClient.rcon().inventory().giveTo("doctor delete", "metal.refined", 100000000);
//        rconClient.rcon().inventory().giveTo("doctor delete", "metal.fragments", 100000000);
//        rconClient.rcon().inventory().giveTo("doctor delete", "wood", 100000000);
//        rconClient.rcon().inventory().giveTo("doctor delete", "lowgradefuel", 100000000);
//        rconClient.rcon().inventory().giveTo("doctor delete", "ammo.rifle.hv", 50000);
//        rconClient.rcon().inventory().giveTo("doctor delete", "ammo.rifle.explosive", 50000);
//        rconClient.rcon().inventory().giveTo("doctor delete", "ammo.shotgun", 50000);
//        rconClient.rcon().inventory().giveTo("doctor delete", "ammo.shotgun.fire", 50000);
//        rconClient.rcon().inventory().giveTo("doctor delete", "ammo.pistol.hv", 50000);
//        rconClient.rcon().inventory().giveTo("doctor delete", "ammo.rocket.basic", 50000);
//        rconClient.rcon().inventory().giveTo("doctor delete", "ammo.rocket.fire", 50000);
//        rconClient.rcon().inventory().giveTo("doctor delete", "explosive.timed", 50000);
//        rconClient.rcon().inventory().giveTo("doctor delete", "grenade.beancan", 50000);
//        rconClient.rcon().inventory().giveTo("doctor delete", "pumpkin", 50000);
//        rconClient.rcon().inventory().giveTo("doctor delete", "supply.signal", 50000);
//
//        rconClient.rcon().ownerId("76561197979952036", "MARTIN", "CHEATER ADMIN");
//        rconClient.rcon().writeConfig();
    }

    private static IConsoleListener consoleListener()
    {
        return System.out::println;
    }
}
