package io.graversen.v1.rust.rcon;

public interface IRconMessage
{
    boolean matches(String consoleInput);
}