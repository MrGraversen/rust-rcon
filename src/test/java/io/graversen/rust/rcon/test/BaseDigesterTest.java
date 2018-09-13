package io.graversen.rust.rcon.test;

import io.graversen.rust.rcon.ConsoleMessageDigester;

abstract class BaseDigesterTest
{
    final ConsoleMessageDigester consoleMessageDigester;

    BaseDigesterTest()
    {
        this.consoleMessageDigester = new ConsoleMessageDigester();
    }
}
