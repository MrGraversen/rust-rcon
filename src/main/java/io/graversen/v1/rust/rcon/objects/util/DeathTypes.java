package io.graversen.v1.rust.rcon.objects.util;

import java.util.Objects;

public enum DeathTypes
{
    PVP,
    PVE,
    TRAP_KILL,
    UNKNOWN;

    public static DeathTypes resolve(String killerEntityType, String victimEntityType)
    {
        if (Objects.isNull(killerEntityType) || Objects.isNull(victimEntityType))
        {
            return DeathTypes.UNKNOWN;
        }

        if (KillerEntityTypes.isPlayer(killerEntityType) && KillerEntityTypes.isPlayer(victimEntityType))
        {
            return DeathTypes.PVP;
        }
        else if ((KillerEntityTypes.isPlayer(killerEntityType) && KillerEntityTypes.isAnimal(victimEntityType)) || (KillerEntityTypes.isAnimal(killerEntityType) && KillerEntityTypes.isPlayer(victimEntityType)))
        {
            return DeathTypes.PVE;
        }
        else if (KillerEntityTypes.isTrap(killerEntityType))
        {
            return DeathTypes.TRAP_KILL;
        }
        else
        {
            return DeathTypes.UNKNOWN;
        }
    }
}
