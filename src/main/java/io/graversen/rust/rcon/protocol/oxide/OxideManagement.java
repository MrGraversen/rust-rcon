package io.graversen.rust.rcon.protocol.oxide;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface OxideManagement {
    CompletableFuture<List<OxidePlugin>> oxidePlugins();
}
