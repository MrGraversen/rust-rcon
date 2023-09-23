package io.graversen.rust.rcon.protocol;

import io.graversen.rust.rcon.RustRconResponse;
import io.graversen.rust.rcon.RustRconRouter;
import io.graversen.rust.rcon.protocol.oxide.DefaultOxideCodec;
import io.graversen.rust.rcon.protocol.oxide.OxideCodec;
import io.graversen.rust.rcon.util.Lazy;
import lombok.NonNull;
import org.apache.commons.text.StringSubstitutor;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class DefaultRustCodec implements Codec {
    private final @NonNull RustRconRouter rustRconRouter;
    private final @NonNull Lazy<AdminCodec> adminCodec;
    private final @NonNull Lazy<SettingsCodec> settingsCodec;
    private final @NonNull Lazy<EventCodec> eventCodec;
    private final @NonNull Lazy<OxideCodec> oxideCodec;

    public DefaultRustCodec(@NonNull RustRconRouter rustRconRouter) {
        this.rustRconRouter = rustRconRouter;
        this.adminCodec = Lazy.of(() -> new DefaultAdminCodec(rustRconRouter));
        this.settingsCodec = Lazy.of(() -> new DefaultSettingsCodec(rustRconRouter));
        this.eventCodec = Lazy.of(() -> new DefaultEventCodec(rustRconRouter));
        this.oxideCodec = Lazy.of(() -> new DefaultOxideCodec(rustRconRouter));
    }

    @Override
    public AdminCodec admin() {
        return adminCodec.get();
    }

    @Override
    public SettingsCodec settings() {
        return settingsCodec.get();
    }

    @Override
    public EventCodec event() {
        return eventCodec.get();
    }

    @Override
    public OxideCodec oxide() {
        return oxideCodec.get();
    }

    @Override
    public CompletableFuture<RustRconResponse> send(@NonNull RustRconMessage rustRconMessage) {
        return rustRconRouter.send(rustRconMessage);
    }

    @Override
    public <T> CompletableFuture<T> send(@NonNull RustRconMessage rustRconMessage, @NonNull Function<RustRconResponse, T> mapper) {
        return rustRconRouter.send(rustRconMessage, mapper);
    }

    @Override
    public RustRconMessage raw(@NonNull String template, @NonNull Map<String, String> values) {
        return compile(template, values);
    }

    protected RustRconMessage compile(@NonNull String template) {
        return compile(template, Map.of());
    }

    protected RustRconMessage compile(@NonNull String template, @NonNull Map<String, String> values) {
        final var compiledTemplate = StringSubstitutor.replace(template, values);
        return new SimpleRustRconMessage(compiledTemplate);
    }
}
