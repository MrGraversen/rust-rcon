package io.graversen.rust.rcon.protocol.util;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.apache.commons.text.StringSubstitutor;

import java.util.Map;
import java.util.function.Supplier;

@Value
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class RconPayload implements Supplier<String> {
    @NonNull String payload;

    static RconPayload raw(@NonNull String payload) {
        return new RconPayload(payload);
    }

    static RconPayload build(@NonNull String payloadTemplate, Object... args) {
        final var payload = String.format(payloadTemplate, args);
        return new RconPayload(payload);
    }

    static RconPayload build(@NonNull String payloadTemplate, Map<String, Object> args) {
        final var payload = StringSubstitutor.replace(payloadTemplate, args);
        return new RconPayload(payload);
    }

    @Override
    public String get() {
        return payload;
    }
}
