package io.graversen.examples.rust.rcon;

import io.graversen.rust.rcon.Constants;
import io.graversen.rust.rcon.RconMessageTypes;
import io.graversen.rust.rcon.events.implementation.ChatMessageEventParser;
import io.graversen.rust.rcon.events.implementation.PlayerDeathEventParser;
import io.graversen.rust.rcon.events.implementation.PlayerSpawnedEventParser;
import io.graversen.rust.rcon.events.types.game.SaveEvent;
import io.graversen.rust.rcon.events.types.player.ChatMessageEvent;
import io.graversen.rust.rcon.events.types.custom.PlayerDeathEvent;
import io.graversen.rust.rcon.events.types.player.PlayerSpawnedEvent;
import io.graversen.rust.rcon.objects.RconReceive;
import io.graversen.rust.rcon.objects.rust.Player;
import io.graversen.rust.rcon.polling.IPlayerPollingListener;
import io.graversen.rust.rcon.rustclient.RustClient;
import io.graversen.rust.rcon.serialization.DefaultSerializer;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class RustRconApp
{
    public static void main(String[] args) throws Exception
    {
        final RustClient rustClient = RustClient.builder()
                .connectTo("graversen.io", Constants.rconPassword(), 30204)
                .build();

//        rustClient.addEventHandling(SaveEvent.class, RconMessageTypes.SAVE_EVENT, new SaveEventParser(), event -> System.out.println("It's a fucking save event!"));

//        rustClient.addEventHandling(
//                ChatMessageEvent.class,
//                RconMessageTypes.CHAT,
//                new ChatMessageEventParser(),
//                event -> System.out.println(String.format("It's a fucking chat event: %s", event.getChatMessage()))
//        );
//
//        rustClient.addEventHandling(
//                PlayerSpawnedEvent.class,
//                RconMessageTypes.PLAYER_SPAWNED,
//                new PlayerSpawnedEventParser(),
//                event -> System.out.println(String.format("En eller anden mf'er har spawned: %s", event.getPlayerName()))
//        );
//

        rustClient.open();

//        rustClient.rcon().info().getBuildInfo();

//        rustClient.addEventHandling(
//                PlayerDeathEvent.class,
//                RconMessageTypes.PLAYER_DEATH,
//                new PlayerDeathEventParser(),
//                event -> System.out.println(new DefaultSerializer().serialize(event))
//        );


//        final CompletableFuture<RconReceive> completableFuture = rustClient.sendAsync("buildinfo");
//        final RconReceive rconReceive = completableFuture.get(1, TimeUnit.MINUTES);
//        System.out.println("Async Result: " + rconReceive.getMessage());
    }

    private static IPlayerPollingListener playerPollingListener()
    {
        return players -> System.out.println("Players: " + players.stream().map(Player::getDisplayName).collect(Collectors.joining(", ")));
    }
}
