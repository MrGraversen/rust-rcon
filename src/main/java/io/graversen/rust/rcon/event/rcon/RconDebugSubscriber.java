package io.graversen.rust.rcon.event.rcon;

import com.google.common.eventbus.Subscribe;
import io.graversen.rust.rcon.event.BaseEventHandler;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RconDebugSubscriber extends BaseEventHandler {
    @Subscribe
    public void onRconProtocolExchangeEvent(@NonNull RconProtocolExchangeEvent event) {
        handleEvent(event);
        log.debug("{}", event);
    }

    @Subscribe
    public void onRconReceivedEvent(@NonNull RconReceivedEvent event) {
        handleEvent(event);
        log.debug("{}", event);
    }
}
