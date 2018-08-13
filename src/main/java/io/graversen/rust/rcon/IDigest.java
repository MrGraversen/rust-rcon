package io.graversen.rust.rcon;

public interface IDigest {
    boolean deepMatches(String consoleInput);
    boolean matches(String consoleInput);
}