package io.graversen.rust.rcon;

public interface IRconMessage
{
    boolean matches(String consoleInput);
}