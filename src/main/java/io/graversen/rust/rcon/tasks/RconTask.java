package io.graversen.rust.rcon.tasks;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class RconTask implements Runnable {
    @Override
    public void run() {
        log.debug("Executing task: {}", name());

        try {
            execute();
        } catch (Exception e) {
            log.error(String.format("Task '%s' failed: %s", name(), e.getMessage()), e);
        }
    }

    protected String name() {
        return this.getClass().getSimpleName();
    }

    protected abstract void execute();
}
