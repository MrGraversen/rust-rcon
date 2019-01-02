package io.graversen.rust.rcon.test;

import io.graversen.rust.rcon.DefaultConsoleParser;

abstract class BaseDefaultParserTest
{
    final DefaultConsoleParser defaultConsoleParser;

    BaseDefaultParserTest()
    {
        this.defaultConsoleParser = new DefaultConsoleParser();
    }
}
