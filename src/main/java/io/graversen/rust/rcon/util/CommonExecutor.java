package io.graversen.rust.rcon.util;

import lombok.NonNull;
import lombok.Synchronized;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@UtilityClass
public class CommonExecutor {
    private static final String GROUP_NAME = "rust";
    private static ScheduledExecutorService instance;

    @Synchronized
    public static ScheduledExecutorService getInstance() {
        if (instance == null) {
            final var processors = CommonUtils.processors();
            log.debug("Initialising common ExecutorService instance with {} workers", processors);
            instance = Executors.newScheduledThreadPool(processors, namedThreadFactory(new AtomicInteger()));
        }

        return instance;
    }

    private static ThreadFactory namedThreadFactory(@NonNull AtomicInteger indexer) {
        return runnable -> new Thread(runnable, getName(indexer));
    }

    private static String getName(@NonNull AtomicInteger indexer) {
        return String.format("%s-worker-%d", GROUP_NAME, indexer.getAndIncrement());
    }
}
