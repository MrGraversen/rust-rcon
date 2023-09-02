package io.graversen.rust.rcon;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(onConstructor_ = @JsonCreator)
public class RustRconRequestDTO {
    @JsonProperty("Identifier")
    private final @NonNull Integer identifier;
    @JsonProperty("Message")
    private final @NonNull String message;
    @JsonProperty("Name")
    private final @NonNull String name;
}
