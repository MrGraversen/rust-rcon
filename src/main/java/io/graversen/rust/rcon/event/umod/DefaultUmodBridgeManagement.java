package io.graversen.rust.rcon.event.umod;

import io.graversen.rust.rcon.protocol.oxide.OxideManagement;
import io.graversen.rust.rcon.protocol.oxide.OxidePlugin;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class DefaultUmodBridgeManagement implements UmodBridgeManagement {
    private final @NonNull OxideManagement oxideManagement;

    @Override
    public CompletableFuture<Optional<OxidePlugin>> rustRconBridgePlugin() {
        return oxideManagement.oxidePlugins()
                .thenApply(plugins -> plugins.stream()
                        .filter(this::isRustRconBridgePlugin)
                        .findFirst());
    }

    @Override
    public CompletableFuture<Boolean> isRustRconBridgeInstalled() {
        return rustRconBridgePlugin().thenApply(Optional::isPresent);
    }

    private boolean isRustRconBridgePlugin(@NonNull OxidePlugin plugin) {
        return RustRconBridgePlugin.PLUGIN_NAME.equalsIgnoreCase(plugin.getPluginName())
                || RustRconBridgePlugin.PLUGIN_FILE.equalsIgnoreCase(plugin.getPluginFile());
    }
}
