package io.graversen.rust.rcon.test;

import io.graversen.rust.rcon.objects.rust.ISteamPlayer;

abstract class BaseDefaultParserTest
{
    protected ISteamPlayer defaultPlayer = () -> "1234";
}
