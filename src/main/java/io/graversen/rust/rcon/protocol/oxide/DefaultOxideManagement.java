package io.graversen.rust.rcon.protocol.oxide;

import io.graversen.rust.rcon.RustRconResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class DefaultOxideManagement implements OxideManagement {
    private static final String PLUGINS_REGEX_STRING = "\\d+\\s+\"(.*?)\"\\s+\\((.*?)\\)\\s+by\\s+(.*?)\\s+\\(.*?\\)\\s+-\\s+(.*?)$";
    private static final Pattern PLUGINS_REGEX = Pattern.compile(PLUGINS_REGEX_STRING, Pattern.MULTILINE);

    private final @NonNull OxideCodec oxideCodec;

    @Override
    public CompletableFuture<List<OxidePlugin>> oxidePlugins() {
        return oxideCodec.oxidePlugins().thenApply(mapOxidePlugins());
    }

    Function<RustRconResponse, List<OxidePlugin>> mapOxidePlugins() {
        return rconResponse -> {
            final var message = rconResponse.getMessage();
            final var matcher = PLUGINS_REGEX.matcher(message);

            final var plugins = new ArrayList<OxidePlugin>();
            while (matcher.find()) {
                String pluginName = matcher.group(1);
                String pluginVersion = matcher.group(2);
                String pluginAuthor = matcher.group(3);
                String pluginFile = matcher.group(4);

                plugins.add(new OxidePlugin(pluginName, pluginVersion, pluginAuthor, pluginFile));
            }

            return List.copyOf(plugins);
        };
    }
}
