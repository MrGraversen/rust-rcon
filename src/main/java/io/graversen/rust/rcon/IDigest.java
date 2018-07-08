package io.graversen.rust.rcon;

public interface IDigest {
    boolean matches(String consoleInput);
}