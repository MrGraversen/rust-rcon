package io.graversen.v1.rust.rcon.listeners;

@FunctionalInterface
public interface IConsoleListener
{
    void onConsoleMessage(String consoleMessage);
}
