package io.graversen.rust.rcon.protocol;

import io.graversen.rust.rcon.objects.util.Animals;
import io.graversen.rust.rcon.rustclient.IRconClient;

public class SettingsRcon extends BaseRcon
{
    SettingsRcon(IRconClient rconClient)
    {
        super(rconClient);
    }

//    public void disableDecay()
//    {
//        setDecayScale(0);
//        setDecayUpkeep(false);
//    }
//
//    public void resetDecay()
//    {
//        setDecayScale(100);
//        setDecayUpkeep(true);
//    }

    public RconEntity decayScale(int decayPercent)
    {
        return rconEntity("decay.scale %d", decayPercent);
    }

    public RconEntity decayUpkeepEnabled(boolean upkeep)
    {
        return rconEntity("decay.upkeep %s", String.valueOf(upkeep));
    }

    public RconEntity stabilityEnabled(boolean enabled)
    {
        return rconEntity("server.stability %s", String.valueOf(enabled));
    }

    public RconEntity radiationEnabled(boolean enabled)
    {
        return rconEntity("server.radiation %s", String.valueOf(enabled));
    }

    public RconEntity globalChatEnabled(boolean enabled)
    {
        return rconEntity("server.globalchat %s", String.valueOf(enabled));
    }

    /**
     * @param population Population active on the server, per square km
     */
    public RconEntity adjustPopulation(Animals animal, int population)
    {
        return rconEntity("%s.population %s", animal.name().toLowerCase(), String.valueOf(population));
    }
}
