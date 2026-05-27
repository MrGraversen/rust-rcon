package io.graversen.rust.rcon.event.umod;

import io.graversen.rust.rcon.protocol.oxide.OxideManagement;
import io.graversen.rust.rcon.protocol.oxide.OxidePlugin;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

class DefaultUmodBridgeManagementTest {
    @Test
    void detectsRustRconBridgeByPluginName() {
        final var bridgePlugin = new OxidePlugin("RustRconBridge", "0.0.1", "Ownzone", "SomeRenamedFile.cs");
        final var bridgeManagement = new DefaultUmodBridgeManagement(oxideManagement(List.of(bridgePlugin)));

        final var plugin = bridgeManagement.rustRconBridgePlugin().join();

        assertTrue(plugin.isPresent());
        assertEquals(bridgePlugin, plugin.get());
        assertTrue(bridgeManagement.isRustRconBridgeInstalled().join());
    }

    @Test
    void detectsRustRconBridgeByPluginFile() {
        final var bridgePlugin = new OxidePlugin("Rust RCON Bridge", "0.0.1", "Ownzone", "RustRconBridge.cs");
        final var bridgeManagement = new DefaultUmodBridgeManagement(oxideManagement(List.of(bridgePlugin)));

        final var plugin = bridgeManagement.rustRconBridgePlugin().join();

        assertTrue(plugin.isPresent());
        assertEquals(bridgePlugin, plugin.get());
        assertTrue(bridgeManagement.isRustRconBridgeInstalled().join());
    }

    @Test
    void returnsEmptyWhenRustRconBridgeIsMissing() {
        final var bridgeManagement = new DefaultUmodBridgeManagement(oxideManagement(List.of(
                new OxidePlugin("Welcomer", "2.1.0", "Dana", "Welcomer.cs"),
                new OxidePlugin("Vanish", "1.6.6", "Whispers88", "Vanish.cs")
        )));

        assertTrue(bridgeManagement.rustRconBridgePlugin().join().isEmpty());
        assertFalse(bridgeManagement.isRustRconBridgeInstalled().join());
    }

    private OxideManagement oxideManagement(List<OxidePlugin> plugins) {
        return () -> CompletableFuture.completedFuture(plugins);
    }
}
