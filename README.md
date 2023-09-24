# Rust RCON :video_game:
An asynchronous, fault-tolerant Rust RCON client built in Java.  
Seamlessly integrates with Rust's RCON request-response semantics using websocket connections.  
Ideal for those looking to harness the power of Rust's RCON with Java's portability and ease.

## About :pencil:

This library serves as a Rust (the video game) counterpart to the [**`minecraft-rcon`**](https://github.com/MrGraversen/minecraft-rcon) library.

It empowers applications with easy integration capabilities with a Rust game server via RCON, translating internal server details into actionable events.

By interfacing with Rust's native RCON payloads, the library efficiently captures and translates in-game occurrences, such as player interactions including connections, chats, combats, and more, into events. This reactive design allows user-code to be promptly informed of player-driven actions.

Additionally, Rust RCON manages connection states intelligently, eliminating concerns about game server restarts. The connection is resilient; if the game server goes offline, it seamlessly recovers once the server becomes available again.

### About RCON

RCON (Remote Console) is a protocol that allows for remote game server management.  
In the context of Rust, RCON serves as a potent tool for administrators and third-party applications to interact with and control game servers. Whether it's for issuing commands, retrieving information, or automating tasks, Rust's RCON implementation provides a secure and efficient interface for these operations, all without requiring direct access to the game server. This library, Rust RCON, simplifies and enhances the interaction with Rust's RCON system, offering a robust solution for various use cases.

## Installation :floppy_disk:

Rust RCON is available through Maven from GitHub Packages. To install:

:warning: **Even though this project is public, you still need to authenticate properly with GitHub Packages**

From the documentation:
> If you want to download and use a package from a public repository, you don't need access to the repository. However, you must be authenticated to GitHub Packages under a user account that has a GitHub Free plan.

You must create a GitHub Personal Access Token to facilitate this access. It must have at least the `read:packages` scope.

[**Click here**](https://github.com/settings/tokens/new?scopes=read:packages&description=Rust+Rcon+GitHub+Packages+Access) to easily create one.

1. Configure Maven settings.xml: Place this token in your ~/.m2/settings.xml (or the equivalent on your system).

```xml
<servers>
  <server>
    <id>github</id>
    <username>YOUR_GITHUB_USERNAME</username>
    <password>YOUR_PERSONAL_ACCESS_TOKEN</password>
  </server>
</servers>
```

2. Add the GitHub Packages repository in your `pom.xml`:

```xml
<repositories>
    <repository>
        <id>github</id>
        <url>https://maven.pkg.github.com/MrGraversen/rust-rcon</url>
    </repository>
</repositories>
```

2. Next, add the Rust RCON dependency:

```xml
<dependency>
    <groupId>io.graversen</groupId>
    <artifactId>rust-rcon</artifactId>
    <version>${io.graversen.rust.rcon-version}</version>
</dependency>
```

3. Now you can use Rust RCON in your Java project!

## Events :rocket:

Rust RCON offers a plethora of events that give developers a detailed insight into various facets of the Rust game server. Below is a categorized breakdown of these events:

### Oxide

- **OxidePluginEvent**: Pertains to activities and notifications related to Oxide plugins.

### Player

- **PlayerChatEvent**: Triggered when a player sends a chat message in the game.
- **PlayerConnectedEvent**: Fired when a player successfully connects to the server.
- **PlayerDeathEvent**: Captured when a player meets their demise in the game.
- **PlayerDisconnectedEvent**: Emitted when a player disconnects from the server.
- **PlayerMiniCopterCrashedEvent**: Indicates when a player's mini-copter crashes.
- **PlayerSuicideEvent**: Signaled when a player deliberately ends their in-game life.

### RCON

- **RconProtocolExchangeEvent**: Deals with the exchange of protocol-specific data via RCON.
- **RconReceivedEvent**: Emitted when the server sends a response or data through RCON.

### Server

- **EasyAntiCheatEvent**: Relates to notifications and details stemming from the EasyAntiCheat system.
- **ItemDisappearedEvent**: Triggered when an in-game item disappears due to various reasons.
- **SaveEvent**: Captured whenever there's a save action on the server, be it automatic or manual.
- **WorldEvent**: Encompasses events related to the overall game world and its elements.
- **ServerEvent**: General server-related events not categorized under other specific events.

### Websocket

- **WsOpenedEvent**: Signaled when a new websocket connection is successfully established.
- **WsMessageEvent**: Fired when a message is received over the websocket connection.
- **WsErrorEvent**: Triggered when there's an error in the websocket communication.
- **WsClosedEvent**: Emitted when the websocket connection is closed, either due to errors or deliberate actions.

## Examples :alembic:

*Coming soon*

## Useful Resources
* https://steamid.io/ - translate between Steam IDs
* https://www.corrosionhour.com/rust-item-list/ - all Rust items, including shortcodes required by inventory rcon
