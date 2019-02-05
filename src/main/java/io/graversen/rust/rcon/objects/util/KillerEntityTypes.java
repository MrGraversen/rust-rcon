package io.graversen.rust.rcon.objects.util;

public class KillerEntityTypes
{
    private KillerEntityTypes()
    {
    }

    public static boolean isAnimal(String killerEntityType)
    {
        return killerEntityType.equalsIgnoreCase("animal") || Animals.parse(killerEntityType) != Animals.UNKNOWN;
    }

    public static boolean isTrap(String killerEntityType)
    {
        return killerEntityType.equalsIgnoreCase("trap") || Traps.parse(killerEntityType) != Traps.UNKNOWN;
    }

    public static boolean isPlayer(String killerEntityType)
    {
        return killerEntityType.equalsIgnoreCase("player");
    }
}
