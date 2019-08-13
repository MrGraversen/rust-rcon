package io.graversen.rust.rcon;

import io.graversen.rust.rcon.objects.util.DeathTypes;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DeathTypesTest
{
    @Test
    void test_resolve_1()
    {
        final DeathTypes deathType = DeathTypes.resolve("Player", "Player");
        assertEquals(DeathTypes.PVP, deathType);
    }

    @Test
    void test_resolve_2()
    {
        final DeathTypes deathType = DeathTypes.resolve("Animal", "Player");
        assertEquals(DeathTypes.PVE, deathType);
    }

    @Test
    void test_resolve_3()
    {
        final DeathTypes deathType = DeathTypes.resolve("Player", "Animal");
        assertEquals(DeathTypes.PVE, deathType);
    }

    @Test
    void test_resolve_4()
    {
        final DeathTypes deathType = DeathTypes.resolve(null, null);
        assertEquals(DeathTypes.UNKNOWN, deathType);
    }

    @Test
    void test_resolve_5()
    {
        final DeathTypes deathType = DeathTypes.resolve("Hello", "World");
        assertEquals(DeathTypes.UNKNOWN, deathType);
    }
}
