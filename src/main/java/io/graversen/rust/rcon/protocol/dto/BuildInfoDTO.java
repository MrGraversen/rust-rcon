package io.graversen.rust.rcon.protocol.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
public class BuildInfoDTO {
    @JsonProperty("Date")
    private final Long dateTime;

    @JsonProperty("Valid")
    private final Boolean valid;

    @JsonProperty("Scm")
    private final ScmDTO scm;

    @JsonProperty("Build")
    private final BuildDTO build;
}
