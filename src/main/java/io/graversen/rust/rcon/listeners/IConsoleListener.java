package io.graversen.rust.rcon.listeners;

@FunctionalInterface
public interface IConsoleListener {
    void onConsoleMessage(String consoleMessage);
}
