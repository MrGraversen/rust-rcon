package io.graversen.rust.rcon.test;

import io.graversen.rust.rcon.ConsoleMessageDigester;

public abstract class BaseDigesterTest
{
    protected final ConsoleMessageDigester consoleMessageDigester;

    protected BaseDigesterTest()
    {
        this.consoleMessageDigester = new ConsoleMessageDigester();
    }
}
