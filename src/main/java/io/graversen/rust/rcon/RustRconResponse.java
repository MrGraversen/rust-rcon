package io.graversen.rust.rcon;

import lombok.*;

import javax.annotation.Nullable;
import java.time.LocalDateTime;

@Getter
@ToString
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class RustRconResponse {
    private final @NonNull LocalDateTime createdAt = LocalDateTime.now();
    private final @NonNull Integer identifier;
    private final @NonNull String message;
    private final @NonNull String type;
    private final @NonNull RustServer server;
    private final @Nullable RustRconRequest request;
}
