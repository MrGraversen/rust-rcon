# rust-rcon
An async Rust RCON client in Java.

## About
Rust pendant to my **[minecraft-rcon](https://github.com/MrGraversen/minecraft-rcon)** library, but mostly non-equivalent because Rust internally has switched to an async websocket network infrastructure - and of course the instruction set differs greatly from that of MineCraft.

The key difference being working with websockts means that the Rust server will also push all console messages down the socket, from which we can extrapolate interesting events, representing what is happening on the server in real-time.

Console messages from the Rust server are not currently in a machine-readable format *(sigh)*, therefore this library will perform a great deal of string inspection to determine what is going on - these parsers are extensively unit-test covered.

## Features

* Simple interface to connect to Rust server
* Exposes common RCON commands as Java methods
* Raises common Rust server mechanics as events
* Oxide/uMod permission system support
* Support for extending with mod support
* :construction: Comes with (C#) uMod plugins for player combat data and broadcasting pretty messages to players 

## Events
Below is a description of events raised by *rust-rcon*. The player-centric events will contain basic information, such as the player's name, 64-bit Steam ID, etc.

#### Game
* **SaveEvent**: The server has auto-saved (world data has been flushed to disk)
* **WorldEvent**: Represents interesting world events, e.g. patrol helicopter, air drop, etc.

#### Player
* **PlayerConnectedEvent**: Occurs when a player joins the server
* **PlayerDisconnectedEvent**: Occurs when a player leaves the server
* **ChatMessageEvent**: Occurs when a player sends a chat message (contains the message)
* **PlayerSpawnedEvent**: Occurs after a player has spawned (e.g. after death or connecting)

#### Server
* **RconOpenEvent**: First thing raised upon opening the client
* **RconMessageEvent**: Represents a raw downpipe rcon message
* **RconErrorEvent**: Usually related to the underlying websocket connection
* **RconClosedEvent**: The rcon connection has been terminated

#### Custom
The purpose of this category of events is to provide special-case enriched events for interesting occurrences.  
*These are normally only supported by extra effort - for example installing a uMod plugin.*
* **PlayerDeathEvent**: Represents a player in-game death with many different details - example:
```
{
    "victim": "victory",
    "killer": "Doctor Delete",
    "bodyPart": "Head",
    "distance": 139.09,
    "hp": 100,
    "weapon": "L96 Rifle",
    "attachments": ["8x Zoom Scope", "Weapon Lasersight"],
    "deathType": "PVP",
    "damageType": "BULLET",
}
```

## :alembic: Examples :zap:
The following examples are also found in the `io.graversen.examples.rust.rcon` package.

### Example 1 - Getting to know your Rust server
In the followig example I demonstrate the most barebones possible way of attaching to a remote or local Rust server.
```java
final RustClient rustClient = RustClient.builder()
    .connectTo("localhost", "awesome_rcon")
    .build();

// Let's connect!
rustClient.open();
```

### Example 2 - a more advanced example
This example is effectively equivalent to *Example 1*; it demonstrates the extensibility of the `RustClient`. You can specify custom implementations of essentially all the inner workings of the `RustClient`.
```java
final RustClient rustClient = RustClient.builder()
    .connectTo("localhost", "awesome_rcon", 12345)
    .withSerializer(new DefaultSerializer())
    .withEventBus(new DefaultEventBus())
    .withLogger(new DefaultLogger(RustClient.class))
    .withRconMessageParser(new DefaultRconMessageParser())
    .build();

    // Let's connect!
    rustClient.open();
```

### Example 3 - let's increase the God Complex
We can manage items using Rust's internal "shortcode" system.
```java
final InventoryRcon inventory = rustClient.rcon().inventory();

// Let's give this player a reward for being awesome
inventory.giveTo(() -> "76561197979952036", "metal.refined", 1000).execute();

// Actually - all players are awesome!
inventory.giveAll("xmas.present.large", 1).execute();
```

### Example 4 - extrapolating cool stuff from the Rust server
We can subscribe to events representing interesting occurrences on the Rust server. You can specify custom parsers for Rust's internal messages.

```java
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

```

```java
private static void logPlayerSpawned(PlayerSpawnedEvent event)
{
    // Log player joins and respawns. Who joined the server most during this month?
    System.out.printf("An awesome player named %s has just joined!", event.getPlayerName());
}
```

```java
private static void logPlayerFight(PlayerDeathEvent event)
{
    // Generate boards displaying the most violent and peaceful players
    // Or the most popular weapons and attachment combinations
    // Who died the most, and who should you put a bounty on?
    System.out.printf("%s shot %s using a %s!", event.getKiller(), event.getVictim(), event.getWeapon());
}
```

### Example 5 - keeping track of players
We may continuously poll the server and receive callbacks for player listings 

```java
// You will get a callback invocation at the specified interval
rustClient.addPlayerPoller(playerPollingListener(), 5000, TimeUnit.MILLISECONDS);
```

```java
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
```

## :memo: Useful Resources
* https://steamid.io/ - translate between Steam IDs
* https://www.corrosionhour.com/rust-item-list/ - all Rust items, including shortcodes required by inventory rcon
