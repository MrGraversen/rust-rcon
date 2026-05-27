package io.graversen.rust.rcon.event.umod;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
public class UmodBridgeEnvelope {
    private final Integer schemaVersion;
    private final String eventType;
    private final String eventId;
    private final String timestamp;
    private final JsonNode payload;
}
