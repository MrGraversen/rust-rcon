package io.graversen.rust.rcon;

public class AuthenticationException extends RuntimeException
{
    public AuthenticationException()
    {
        super("Invalid RCON password");
    }
}
