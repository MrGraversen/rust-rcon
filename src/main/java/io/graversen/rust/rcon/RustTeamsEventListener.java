package io.graversen.rust.rcon;

import com.google.common.eventbus.Subscribe;
import io.graversen.rust.rcon.event.server.RustTeamsEvent;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.function.Consumer;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class RustTeamsEventListener {
    private final @NonNull Consumer<List<RustTeam>> rustTeamsConsumer;

    @Subscribe
    public void onRustTeams(RustTeamsEvent rustTeamsEvent) {
        rustTeamsConsumer.accept(rustTeamsEvent.getRustTeams());
    }
}
