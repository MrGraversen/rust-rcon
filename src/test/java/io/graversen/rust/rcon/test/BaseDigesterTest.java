package io.graversen.rust.rcon.test;

import io.graversen.rust.rcon.DefaultConsoleParser;

abstract class BaseDigesterTest
{
    final DefaultConsoleParser defaultConsoleParser;

    BaseDigesterTest()
    {
        this.defaultConsoleParser = new DefaultConsoleParser();
    }
}
