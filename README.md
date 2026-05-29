# Rust RCON 🎮

An asynchronous, fault-tolerant Rust RCON client built in Java.

Rust RCON connects to a Rust game server over WebRCON, exposes command codecs for common admin tasks, and translates server output into application-friendly events. It is designed for server tooling, dashboards, automation, and admin assistants that need a resilient connection to a live Rust server.

## About 📝

This library is the Rust counterpart to [minecraft-rcon](https://github.com/MrGraversen/minecraft-rcon).

Out of the box, Rust RCON works with a vanilla Rust server by parsing events from the RCON stream. For richer event coverage, it can also run in an optional uMod bridge mode. In that mode, a small uMod plugin emits structured event envelopes through the server log, while the Java client continues to use RCON for commands and server interaction.

The connection is resilient. If the game server goes offline or restarts, the client reconnects once the server becomes available again.

## Event Source Strategies

Rust RCON supports two event source strategies:

### RCON

`RCON` is the default strategy and works with vanilla Rust servers. Events are parsed from the normal RCON/log output that Rust exposes.

Use this when compatibility matters most or when the server does not run uMod.

```java
import io.graversen.rust.rcon.DefaultRustRconService;
import io.graversen.rust.rcon.RustRconConfiguration;

public class VanillaRustRconExample {
    public static void main(String[] args) {
        final var configuration = new RustRconConfiguration(
                "localhost",
                28016,
                "rcon-password"
        );

        final var service = new DefaultRustRconService(configuration);
        service.start();
    }
}
```

### UMOD

`UMOD` is an opt-in strategy for servers running uMod. It uses `src/main/umod/RustRconBridge.cs` to emit structured bridge events that the Java client maps into normal Rust RCON events.

In UMOD mode, the server still receives commands through RCON. The difference is event sourcing: events come from the uMod bridge instead of vanilla log parsing.

```java
import io.graversen.rust.rcon.DefaultRustRconService;
import io.graversen.rust.rcon.RustRconConfiguration;
import io.graversen.rust.rcon.event.RustEventSourceStrategy;

public class UmodRustRconExample {
    public static void main(String[] args) {
        final var configuration = new RustRconConfiguration(
                "localhost",
                28016,
                "rcon-password",
                RustEventSourceStrategy.UMOD
        );

        final var service = new DefaultRustRconService(configuration);
        service.start();
    }
}
```

You can check whether the bridge plugin is loaded:

```java
import io.graversen.rust.rcon.DefaultRustRconService;
import io.graversen.rust.rcon.RustRconConfiguration;
import io.graversen.rust.rcon.event.RustEventSourceStrategy;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UmodBridgeCheckExample {
    public static void main(String[] args) {
        final var configuration = new RustRconConfiguration(
                "localhost",
                28016,
                "rcon-password",
                RustEventSourceStrategy.UMOD
        );

        final var service = new DefaultRustRconService(configuration);
        service.start();

        final var isInstalled = service.umodBridgeManagement()
                .isRustRconBridgeInstalled()
                .join();

        log.info("RustRconBridge installed: {}", isInstalled);
        service.stop();
    }
}
```

## Installation 💾

Rust RCON is available through Maven from GitHub Packages.

⚠️ Even though this project is public, GitHub Packages still requires authentication.

From the GitHub documentation:

> If you want to download and use a package from a public repository, you don't need access to the repository. However, you must be authenticated to GitHub Packages under a user account that has a GitHub Free plan.

Create a GitHub Personal Access Token with at least the `read:packages` scope:

[Create token](https://github.com/settings/tokens/new?scopes=read:packages&description=Rust+Rcon+GitHub+Packages+Access)

Configure Maven in `~/.m2/settings.xml`:

```xml
<servers>
  <server>
    <id>github</id>
    <username>YOUR_GITHUB_USERNAME</username>
    <password>YOUR_PERSONAL_ACCESS_TOKEN</password>
  </server>
</servers>
```

Add the GitHub Packages repository:

```xml
<repositories>
    <repository>
        <id>github</id>
        <url>https://maven.pkg.github.com/MrGraversen/rust-rcon</url>
    </repository>
</repositories>
```

Add the dependency:

```xml
<dependency>
    <groupId>io.graversen</groupId>
    <artifactId>rust-rcon</artifactId>
    <version>${io.graversen.rust.rcon-version}</version>
</dependency>
```

## Usage ⚗️

```java
import com.google.common.eventbus.Subscribe;
import io.graversen.rust.rcon.DefaultRustRconService;
import io.graversen.rust.rcon.RustRconConfiguration;
import io.graversen.rust.rcon.event.player.PlayerChatEvent;
import io.graversen.rust.rcon.event.server.WorldEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RustRconEventsExample {
    public static void main(String[] args) throws InterruptedException {
        final var configuration = new RustRconConfiguration(
                "localhost",
                28016,
                "rcon-password"
        );

        final var service = new DefaultRustRconService(configuration);
        service.registerEvents(new RustEventSubscriber());
        service.start();

        Thread.currentThread().join();
    }

    static class RustEventSubscriber {
        @Subscribe
        public void onPlayerChat(PlayerChatEvent event) {
            log.info("{}: {}", event.getPlayerName().get(), event.getMessage());
        }

        @Subscribe
        public void onWorldEvent(WorldEvent event) {
            log.info("{} {}", event.getEvent(), event.getAttributes());
        }
    }
}
```

The service also exposes management APIs for server info, players, teams, Oxide/uMod plugins, and command codecs:

```java
import io.graversen.rust.rcon.DefaultRustRconService;
import io.graversen.rust.rcon.RustRconConfiguration;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RustRconManagementExample {
    public static void main(String[] args) {
        final var configuration = new RustRconConfiguration(
                "localhost",
                28016,
                "rcon-password"
        );

        final var service = new DefaultRustRconService(configuration);
        service.start();

        final var serverInfo = service.serverInfo().join();
        final var players = service.players();
        final var teams = service.teams();
        final var adminCodec = service.codec().admin();
        final var oxideCodec = service.codec().oxide();

        log.info("Server info: {}", serverInfo);
        log.info("Players: {}", players);
        log.info("Teams: {}", teams);
        log.info("Admin codec: {}", adminCodec);
        log.info("Oxide codec: {}", oxideCodec);

        service.stop();
    }
}
```

## Events 🚀

Rust RCON exposes events through a small event bus. Supported events depend on the configured event source strategy.

You can inspect supported events at runtime:

```java
import io.graversen.rust.rcon.DefaultRustRconService;
import io.graversen.rust.rcon.RustRconConfiguration;
import io.graversen.rust.rcon.event.player.PlayerChatEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RustRconCapabilitiesExample {
    public static void main(String[] args) {
        final var configuration = new RustRconConfiguration(
                "localhost",
                28016,
                "rcon-password"
        );

        final var service = new DefaultRustRconService(configuration);
        final var capabilities = service.eventCapabilities();

        log.info("Event strategy: {}", capabilities.getStrategy());
        log.info("Supports chat events: {}", capabilities.supports(PlayerChatEvent.class));
    }
}
```

### RCON Events

The default RCON strategy supports events that can be inferred from vanilla Rust server output.

Player events:

- `PlayerChatEvent`
- `PlayerConnectedEvent`
- `PlayerDeathEvent`
- `PlayerDisconnectedEvent`
- `PlayerMiniCopterCrashedEvent`
- `PlayerSuicideEvent`

Server and world events:

- `EasyAntiCheatEvent`
- `EntityCommandEvent`
- `ItemDisappearedEvent`
- `SaveEvent`
- `ServerInfoEvent`
- `WorldEvent`

Oxide log events:

- `OxidePluginEvent`

RCON and websocket events:

- `RconProtocolExchangeEvent`
- `RconReceivedEvent`
- `WsOpenedEvent`
- `WsMessageEvent`
- `WsErrorEvent`
- `WsClosedEvent`

### UMOD Bridge Events

The UMOD strategy emits structured bridge events through `RustRconBridge.cs`. It currently supports the following event families.

Player activity:

- `PlayerChatEvent`
- `PlayerConnectedEvent`
- `PlayerDisconnectedEvent`
- `PlayerDeathEvent`
- `PlayerRespawnedEvent`
- `PlayerWoundedEvent`
- `PlayerRecoveredEvent`

Moderation and admin insight:

- `PlayerKickedEvent`
- `PlayerBannedEvent`
- `PlayerUnbannedEvent`
- `PlayerReportedEvent`
- `PlayerViolationEvent`

Server lifecycle:

- `ServerInitializedEvent`
- `SaveEvent`
- `ServerShutdownEvent`

Team and explosive use:

- `TeamEvent`
- `ExplosiveUseEvent`

World and objective events:

- `WorldEvent`

`WorldEvent` includes an event type and optional string attributes. In UMOD mode this covers low-volume, high-signal objective events such as:

- `AIRDROP`
- `SUPPLY_DROP_DROPPED`
- `SUPPLY_DROP_LANDED`
- `CARGO_SHIP_HARBOR_APPROACH`
- `CARGO_SHIP_HARBOR_ARRIVED`
- `CARGO_SHIP_HARBOR_LEFT`
- `LOCKED_CRATE_LANDED`
- `LOCKED_CRATE_HACK_STARTED`
- `LOCKED_CRATE_HACK_COMPLETED`
- `PATROL_HELICOPTER_KILLED`
- `BRADLEY_APC_DESTROYED`
- `MLRS_FIRED`

Bridge diagnostics:

- `UmodBridgeDiagnosticEvent`

Diagnostics are emitted when bridge payloads are malformed, unsupported, or unknown to the Java client.

## uMod Bridge Plugin

The bridge plugin source lives at:

```text
src/main/umod/RustRconBridge.cs
```

Install it like a normal uMod plugin by placing it in the server's uMod plugins folder. Once loaded, it writes structured messages with the `[rust-rcon]` prefix. The Java client consumes those messages only when configured with `RustEventSourceStrategy.UMOD`.

The bridge intentionally focuses on low-volume, high-value signals. It avoids broad hooks such as general entity damage, item pickup, resource gathering, player input, and weapon fire because those can become noisy and expensive on a live server.

## Useful Resources

- https://steamid.io/ - translate between Steam IDs
- https://www.corrosionhour.com/rust-item-list/ - Rust items and short names
- https://umod.org/documentation/games/rust - uMod Rust documentation
- https://docs.oxidemod.com/hooks/ - Oxide/uMod hook reference
