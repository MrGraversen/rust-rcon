package io.graversen.rust.rcon;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(onConstructor_ = @JsonCreator)
public class RustRconResponseDTO {
    @JsonProperty("Identifier")
    private Integer identifier;
    @JsonProperty("Message")
    private String message;
    @JsonProperty("Type")
    private String type;
    @JsonProperty("Stacktrace")
    private String stackTrace;
}
