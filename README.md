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

## :memo: Useful Resources
* https://steamid.io/ - translate between Steam IDs
* https://www.corrosionhour.com/rust-item-list/ - all Rust items, including shortcodes required by inventory rcon
