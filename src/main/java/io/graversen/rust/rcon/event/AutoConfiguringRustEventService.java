package io.graversen.rust.rcon.event;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import io.graversen.rust.rcon.event.rcon.RconReceivedEvent;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
public class AutoConfiguringRustEventService extends BaseEventHandler implements RustEventService {
    private final @NonNull EventBus eventBus;
    private final @NonNull Set<BaseRustEventParser<?>> eventParsers = new HashSet<>();

    @Subscribe
    @Override
    public void onRconReceived(@NonNull RconReceivedEvent event) {
        handleEvent(event);
        final var payload = event.getRconResponse();
        eventParsers.stream()
                .filter(eventParser -> eventParser.supports(payload))
                .map(eventParser -> eventParser.parseEvent(payload))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .peek(parsedEvent -> log.debug("Emitting event {} from payload: {}", parsedEvent, payload.getMessage()))
                .forEach(eventBus::post);
    }

    @Override
    public void configure() {
        log.debug("Configuration begins");
        final var eventParserClasses = Objects.requireNonNullElseGet(eventParserPackages(), Set::<String>of).stream()
                .map(this::findEventParserClasses)
                .flatMap(List::stream)
                .toList();

        log.debug("Found {} {} candidates, trying to initialise them now", eventParserClasses.size(), eventParserSuperClass().getSimpleName());

        eventParserClasses.stream()
                .map(this::instantiateEventParser)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .sorted(Comparator.comparingInt(BaseRustEventParser::order))
                .forEach(eventParsers::add);

        eventBus.register(this);
        log.debug("Configuration completed");
    }

    protected Set<String> eventParserPackages() {
        return Set.of(
                "io.graversen.rust.rcon.event"
        );
    }

    protected Class<?> eventParserSuperClass() {
        return BaseRustEventParser.class;
    }

    private List<Class<?>> findEventParserClasses(@NonNull String packageName) {
        final var classes = new ArrayList<Class<?>>();
        final var reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(packageName))
                .setScanners(new SubTypesScanner()));

        for (Class<?> eventParserSubClass : reflections.getSubTypesOf(eventParserSuperClass())) {
            final var eventParserGenericSuperClass = eventParserSubClass.getGenericSuperclass();
            if (eventParserGenericSuperClass instanceof ParameterizedType parameterizedType) {
                for (Type typeArgument : parameterizedType.getActualTypeArguments()) {
                    if (RustEvent.class.isAssignableFrom((Class<?>) typeArgument)) {
                        classes.add(eventParserSubClass);
                    }
                }
            }
        }

        return classes;
    }

    private Optional<BaseRustEventParser<?>> instantiateEventParser(@NonNull Class<?> eventParserClass) {
        try {
            final var instance = eventParserClass.getDeclaredConstructor().newInstance();
            if (eventParserSuperClass().isAssignableFrom(instance.getClass())) {
                log.debug("Successfully initialised {} instance!", instance.getClass().getSimpleName());
                final var baseEventParser = (BaseRustEventParser<?>) instance;
                return Optional.of(baseEventParser);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return Optional.empty();
    }
}
