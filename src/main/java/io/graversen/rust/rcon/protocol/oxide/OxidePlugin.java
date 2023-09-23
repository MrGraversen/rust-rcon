package io.graversen.rust.rcon.protocol.oxide;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor
public class OxidePlugin {
    @NonNull String pluginName;
    @NonNull String pluginVersion;
    @NonNull String pluginAuthor;
    @NonNull String pluginFile;
}
