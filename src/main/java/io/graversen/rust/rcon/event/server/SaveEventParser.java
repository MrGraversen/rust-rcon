package io.graversen.rust.rcon.event.server;

import io.graversen.rust.rcon.RustRconResponse;
import io.graversen.rust.rcon.event.BaseRustEventParser;
import lombok.NonNull;

import java.util.Optional;
import java.util.function.Function;

public class SaveEventParser extends BaseRustEventParser<SaveEvent> {
    @Override
    public boolean supports(@NonNull RustRconResponse payload) {
        return payload.getMessage().equalsIgnoreCase("saving complete");
    }

    @Override
    protected Function<RustRconResponse, Optional<SaveEvent>> eventParser() {
        return payload -> Optional.of(new SaveEvent(payload.getServer()));
    }

    @Override
    public Class<SaveEvent> eventClass() {
        return SaveEvent.class;
    }
}
