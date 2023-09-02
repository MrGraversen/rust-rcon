package io.graversen.rust.rcon.event.ws;

import com.google.common.eventbus.Subscribe;
import io.graversen.rust.rcon.event.BaseEventHandler;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WsDebugSubscriber extends BaseEventHandler {
    @Subscribe
    public void onWsErrorEvent(@NonNull WsErrorEvent event) {
        handleEvent(event);
        log.debug("{}", event);
    }

    @Subscribe
    public void onWsClosedEvent(@NonNull WsClosedEvent event) {
        handleEvent(event);
        log.debug("{}", event);
    }

    @Subscribe
    public void onWsMessageEvent(@NonNull WsMessageEvent event) {
        handleEvent(event);
        log.debug("{}", event);
    }

    @Subscribe
    public void onWsOpenedEvent(@NonNull WsOpenedEvent event) {
        handleEvent(event);
        log.debug("{}", event);
    }
}
