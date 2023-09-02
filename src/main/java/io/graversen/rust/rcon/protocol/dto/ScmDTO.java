package io.graversen.rust.rcon.protocol.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
public class ScmDTO {
    @JsonProperty("Type")
    private final String type;

    @JsonProperty("ChangeId")
    private final String changeId;

    @JsonProperty("Branch")
    private final String branch;

    @JsonProperty("Repo")
    private final String repository;

    @JsonProperty("Comment")
    private final String comment;

    @JsonProperty("Author")
    private final String author;

    @JsonProperty("Date")
    private final String dateTime;
}
