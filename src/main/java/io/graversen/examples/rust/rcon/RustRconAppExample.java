package io.graversen.examples.rust.rcon;

import io.graversen.fiber.event.bus.DefaultEventBus;
import io.graversen.rust.rcon.Constants;
import io.graversen.rust.rcon.RconMessageTypes;
import io.graversen.rust.rcon.events.implementation.PlayerDeathEventParser;
import io.graversen.rust.rcon.events.implementation.PlayerSpawnedEventParser;
import io.graversen.rust.rcon.events.parsers.DefaultRconMessageParser;
import io.graversen.rust.rcon.events.types.custom.PlayerDeathEvent;
import io.graversen.rust.rcon.events.types.player.PlayerSpawnedEvent;
import io.graversen.rust.rcon.logging.DefaultLogger;
import io.graversen.rust.rcon.objects.rust.Player;
import io.graversen.rust.rcon.polling.IPlayerPollingListener;
import io.graversen.rust.rcon.protocol.InventoryRcon;
import io.graversen.rust.rcon.rustclient.RustClient;
import io.graversen.rust.rcon.serialization.DefaultSerializer;
import io.graversen.rust.rcon.support.AirstrikeMod;
import io.graversen.rust.rcon.support.UmodSupport;

import java.util.concurrent.TimeUnit;

public class RustRconAppExample
{
    private RustRconAppExample()
    {
    }

    public static class ExampleOne
    {
        public static void main(String[] args)
        {
            final RustClient rustClient = RustClient.builder()
                    .connectTo("localhost", "awesome_rcon")
                    .build();

            rustClient.open();
        }
    }

    public static class ExampleTwo
    {
        public static void main(String[] args)
        {
            final RustClient rustClient = RustClient.builder()
                    .connectTo("localhost", "awesome_rcon", 12345)
                    .withSerializer(new DefaultSerializer())
                    .withEventBus(new DefaultEventBus())
                    .withLogger(new DefaultLogger(RustClient.class))
                    .withRconMessageParser(new DefaultRconMessageParser())
                    .build();

            rustClient.open();
        }
    }

    public static class ExampleThree
    {
        public static void main(String[] args)
        {
            final RustClient rustClient = RustClient.builder()
                    .connectTo("graversen.io", Constants.rconPassword(), 30204)
                    .build();

            rustClient.open();

            final InventoryRcon inventory = rustClient.rcon().inventory();

            // Let's give this player a reward for being awesome
            inventory.giveTo(() -> "76561197979952036", "metal.refined", 1000).execute();

            // Actually - all players are awesome!
            inventory.giveAll("xmas.present.large", 1);
        }
    }

    public static class ExampleFour
    {
        public static void main(String[] args)
        {
            final RustClient rustClient = RustClient.builder()
                    .connectTo("localhost", "awesome_rcon")
                    .build();

            rustClient.open();

            // Subscribe to "PlayerSpawnedEvent" using the default parser and a simple listener
            rustClient.addEventHandling(
                    PlayerSpawnedEvent.class,
                    RconMessageTypes.PLAYER_SPAWNED,
                    new PlayerSpawnedEventParser(),
                    ExampleFour::logPlayerSpawned
            );

            // Subscriber to "PlayerDeathEvent" using the default parser and a simple listener
            rustClient.addEventHandling(
                    PlayerDeathEvent.class,
                    RconMessageTypes.PLAYER_DEATH,
                    new PlayerDeathEventParser(),
                    ExampleFour::logPlayerFight
            );
        }

        private static void logPlayerSpawned(PlayerSpawnedEvent event)
        {
            // Log player joins and respawns. Who joined the server most during this month?
            System.out.printf("An awesome player named %s has just joined!", event.getPlayerName());
        }

        private static void logPlayerFight(PlayerDeathEvent event)
        {
            // Generate boards displaying the most violent and peaceful players
            // Or the most popular weapons and attachment combinations
            // Who died the most, and who should you put a bounty on?
            System.out.printf("%s shot %s using a %s!", event.getKiller(), event.getVictim(), event.getWeapon());
        }
    }

    public static class ExampleFive
    {
        public static void main(String[] args)
        {
            final RustClient rustClient = RustClient.builder()
                    .connectTo("localhost", "awesome_rcon")
                    .build();

            rustClient.open();

            // You will get a callback invocation at the specified interval
            rustClient.addPlayerPoller(playerPollingListener(), 5000, TimeUnit.MILLISECONDS);
        }

        private static IPlayerPollingListener playerPollingListener()
        {
            return players -> players.forEach(ExampleFive::logPlayerStatus);
        }

        private static void logPlayerStatus(Player player)
        {
            // You can get really creative here.
            // Maybe a NRT dashboard displaying current players and graphing their network latencies?
            System.out.printf("Player %s has ping %d ms.\n", player.getDisplayName(), player.getPing());
        }
    }

    public static class ExampleSix
    {
        public static void main(String[] args)
        {
            final RustClient rustClient = RustClient.builder()
                    .connectTo("localhost", "awesome_rcon")
                    .build();

            rustClient.open();

            // Let's call in an airdrop
            rustClient.rcon().event().airDrop().execute();

            // Sometimes, certain players need a small slap
            rustClient.rcon().ban(() -> "76561197979952036", "Calm down!").execute();
            rustClient.rcon().deleteAllEntities(() -> "76561197979952036").execute();

            // And sometimes, certain players are just too noisy in-game...
            rustClient.rcon().muteVoice(() -> "76561197979952036").execute();

            // It is also possible to communicate directly over the RCON protocol
            rustClient.sendAsync("global.say \"Hello World\"");
        }
    }

    public static class ExampleSeven
    {
        public static void main(String[] args)
        {
            final RustClient rustClient = RustClient.builder()
                    .connectTo("localhost", "awesome_rcon")
                    .build();

            rustClient.open();

            // Initialize AirStrike mod
            final AirstrikeMod airstrikeMod = new AirstrikeMod(rustClient);

            // (Formerly known as Oxide)
            // Initialize uMod, granting access to manage plugin permissions
            final UmodSupport umodSupport = new UmodSupport(rustClient);

            // Allow a certain player to call air strikes from chat
            umodSupport.grant(airstrikeMod, () -> "76561197979952036", "chat.strike");

            // Call an air strike to a random location
            airstrikeMod.callStrikeRandom(AirstrikeMod.StrikeTypes.STRIKE);

            // You might also automate mod installation on the server using:
            // airstrikeMod.umodDirectLink();
        }
    }
}
