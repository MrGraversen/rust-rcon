package io.graversen.rust.rcon.event.rcon;

import com.google.common.eventbus.Subscribe;
import io.graversen.rust.rcon.event.BaseEventHandler;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.function.BiConsumer;

@RequiredArgsConstructor
public class RconLogSubscriber extends BaseEventHandler {
    private final @NonNull BiConsumer<String, String> logger;

    @Subscribe
    public void onRconReceivedEvent(@NonNull RconReceivedEvent event) {
        handleEvent(event);
        logger.accept(event.getClientName(), event.getRconResponse().getMessage());
    }
}
