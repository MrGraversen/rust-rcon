package io.graversen.rust.rcon;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@RequiredArgsConstructor
public class RustRconRequest {
    private final @NonNull LocalDateTime createdAt = LocalDateTime.now();
    private final @NonNull Integer identifier;
    private final @NonNull String message;
}
