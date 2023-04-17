package io.graversen.v1.rust.rcon;

import io.graversen.v1.rust.rcon.objects.rust.ISteamPlayer;

abstract class BaseDefaultParserTest
{
    protected ISteamPlayer defaultPlayer = () -> "1234";
}
