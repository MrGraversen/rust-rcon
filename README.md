# rust-rcon
A Rust RCON client in Java.

## About
Rust pendant to my **[minecraft-rcon](https://github.com/MrGraversen/minecraft-rcon)** library, but mostly non-equivalent because Rust internally has switched to an async websocket network infrastructure - and of course the instruction set differs greatly from that of MineCraft.

## Features

* Simple interface to connect to Rust server
* Exposes common RCON commands as Java methods
* Raises all common Rust server mechanics as events
* Oxide permission system support
* Support for extending with mod support

## Events
Below is a description of events raised by `rust-rcon`:

* **ChatMessageEvent**: Occurs when a player sends a chat message
* **PlayerConnectedEvent**: Occurs when a player joins the server
* **PlayerDeathEvent**: Represents a player in-game death
* **PlayerDisconnectedEvent**: Occurs when a player leaves the server
* **PlayerSpawnedEvent**: Occurs after a player has spawned (e.g. after death or connecting)
* **SaveEvent**: The server has auto-saved
* **WorldEvent**: Represents interesting world events, e.g. patrol helicopter, air drop, etc.

*To be continued...*
