package io.graversen.rust.rcon.event;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class EventHandler {
    @NonNull String handlerName;
    @NonNull LocalDateTime handledAt = LocalDateTime.now();
}
