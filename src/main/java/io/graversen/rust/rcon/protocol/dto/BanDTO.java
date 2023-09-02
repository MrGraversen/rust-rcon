package io.graversen.rust.rcon.protocol.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
public class BanDTO {
    @JsonProperty("steamid")
    private final String steamId;
    @JsonProperty("username")
    private final String username;
    @JsonProperty("notes")
    private final String notes;
    @JsonProperty("expiry")
    private final Long expiry;
}
