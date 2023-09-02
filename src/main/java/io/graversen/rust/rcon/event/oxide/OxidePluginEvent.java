package io.graversen.rust.rcon.event.oxide;

import io.graversen.rust.rcon.event.RustEvent;
import lombok.*;

@Getter
@ToString
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class OxidePluginEvent extends RustEvent {
    private final @NonNull String pluginName;
    private final @NonNull String message;
}
