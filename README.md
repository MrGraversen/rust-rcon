# rust-rcon
A Rust RCON client in Java.

## About
Rust pendant to my **[minecraft-rcon](https://github.com/MrGraversen/minecraft-rcon)** library, but mostly non-equivalent because Rust internally has switched to an async websocket network infrastructure - and of course the instruction set differs greatly from that of MineCraft.

The key difference being working with websockts means that the Rust server will also push all console messages down the socket, from which we can extrapolate interesting events, representing what is happening on the server in real-time.

Console messages from the Rust server are not currently in a machine-readable format, therefore this library will perform a great deal of string inspection to determine what is going on.

## Features

* Simple interface to connect to Rust server
* Exposes common RCON commands as Java methods
* Raises common Rust server mechanics as events
* Oxide permission system support
* Support for extending with mod support

## Events
Below is a description of events raised by `rust-rcon`. The player-centric events will contain basic information, such as the player's name, 64-bit Steam ID, etc.

* **ChatMessageEvent**: Occurs when a player sends a chat message
* **PlayerConnectedEvent**: Occurs when a player joins the server
* **PlayerDeathEvent**: Represents a player in-game death
* **PlayerDisconnectedEvent**: Occurs when a player leaves the server
* **PlayerSpawnedEvent**: Occurs after a player has spawned (e.g. after death or connecting)
* **SaveEvent**: The server has auto-saved (world data has been flushed to disk)
* **WorldEvent**: Represents interesting world events, e.g. patrol helicopter, air drop, etc.

*To be continued...*
