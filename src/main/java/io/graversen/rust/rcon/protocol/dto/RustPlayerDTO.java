package io.graversen.rust.rcon.protocol.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
public class RustPlayerDTO {
    @JsonProperty("SteamID")
    private final String steamId;

    @JsonProperty("DisplayName")
    private final String playerName;

    @JsonProperty("Ping")
    private final String ping;

    @JsonProperty("Address")
    private final String ipAddress;

    @JsonProperty("ConnectedSeconds")
    private final Integer connectedSeconds;

    @JsonProperty("Health")
    private final Double health;
}
