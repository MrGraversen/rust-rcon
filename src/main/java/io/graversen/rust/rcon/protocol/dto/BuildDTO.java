package io.graversen.rust.rcon.protocol.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
public class BuildDTO {
    @JsonProperty("Id")
    private final String id;

    @JsonProperty("Number")
    private final String number;

    @JsonProperty("Tag")
    private final String tag;

    @JsonProperty("Url")
    private final String url;

    @JsonProperty("Name")
    private final String name;

    @JsonProperty("Node")
    private final String node;
}
