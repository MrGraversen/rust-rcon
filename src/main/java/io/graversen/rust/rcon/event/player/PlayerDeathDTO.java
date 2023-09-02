package io.graversen.rust.rcon.event.player;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
public class PlayerDeathDTO {
    @JsonProperty("victim")
    private final String victim;
    @JsonProperty("killer")
    private final String killer;
    @JsonProperty("bodypart")
    private final String bodyPart;
    @JsonProperty("distance")
    private final String distance;
    @JsonProperty("hp")
    private final String health;
    @JsonProperty("weapon")
    private final String weapon;
    @JsonProperty("attachments")
    private final String attachments;
    @JsonProperty("killerId")
    private final String killerId;
    @JsonProperty("victimId")
    private final String victimId;
    @JsonProperty("damageType")
    private final String damageType;
    @JsonProperty("killerEntityType")
    private final String killerEntityType;
    @JsonProperty("victimEntityType")
    private final String victimEntityType;
    @JsonProperty("owner")
    private final String owner;
}
