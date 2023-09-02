package io.graversen.rust.rcon.tasks;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ReconnectRconTask implements RconTask {
    private final @NonNull Runnable reconnectHandle;

    @Override
    public void run() {
        log.debug("Invoking reconnect handle");
        reconnectHandle.run();
    }
}
