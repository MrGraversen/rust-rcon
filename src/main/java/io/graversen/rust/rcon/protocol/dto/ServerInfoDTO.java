package io.graversen.rust.rcon.protocol.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
public class ServerInfoDTO {
    @JsonProperty("Hostname")
    private final String hostName;

    @JsonProperty("MaxPlayers")
    private final Integer maxPlayers;

    @JsonProperty("Players")
    private final Integer currentPlayers;

    @JsonProperty("Queued")
    private final Integer queuedPlayers;

    @JsonProperty("Joining")
    private final Integer joiningPlayers;

    @JsonProperty("EntityCount")
    private final Integer entityCount;

    @JsonProperty("GameTime")
    private final String gameDateTime;

    @JsonProperty("Uptime")
    private final Long uptimeSeconds;

    @JsonProperty("Map")
    private final String map;

    @JsonProperty("Framerate")
    private final Double frameRate;

    @JsonProperty("Memory")
    private final Integer memoryUsageMb;

    @JsonProperty("Collections")
    private final Integer collections;

    @JsonProperty("NetworkIn")
    private final Integer networkInBytes;

    @JsonProperty("NetworkOut")
    private final Integer networkOutBytes;

    @JsonProperty("Restarting")
    private final Boolean restarting;

    @JsonProperty("SaveCreatedTime")
    private final String saveCreatedTime;

    @JsonProperty("Version")
    private final Integer version;

    @JsonProperty("Protocol")
    private final String protocol;
}
