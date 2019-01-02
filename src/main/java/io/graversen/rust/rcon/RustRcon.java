package io.graversen.rust.rcon;

import io.graversen.rust.rcon.listeners.IConsoleListener;
import io.graversen.rust.rcon.objects.rust.Player;
import io.graversen.rust.rcon.support.AirstrikeMod;
import io.graversen.rust.rcon.util.Colors;

import java.util.List;

public class RustRcon
{
    public static void main(String[] args) throws RconException
    {
        final IRconClient rconClient = RconClient.connect("graversen.io", Constants.rconPassword(), 30204);
        rconClient.attachConsoleListener(consoleListener());
//        rconClient.rcon().say("%s %s %s", "hej", Colors.blue("med"), Colors.green("dig"));
//
        rconClient.rcon().inventory().giveTo("doctor delete", "stones", 100000000);
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

//        final AirstrikeMod airstrikeMod = new AirstrikeMod(rconClient);
//        airstrikeMod.callStrikeRandom(AirstrikeMod.StrikeTypes.STRIKE);
//        airstrikeMod.callStrikeRandom(AirstrikeMod.StrikeTypes.STRIKE);
    }

    private static IConsoleListener consoleListener()
    {
        return System.out::println;
    }
}
