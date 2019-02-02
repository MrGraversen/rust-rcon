package io.graversen.rust.rcon.events.implementation;

import io.graversen.rust.rcon.events.IEventParser;
import io.graversen.rust.rcon.events.types.player.PlayerDeathEvent;

import java.util.Optional;
import java.util.function.Function;

public class PlayerDeathEventParser implements IEventParser<PlayerDeathEvent>
{
    // Examples:
    // [RS] Dylan1273[24163715/76561198071530582] was killed by fireball_small (entity)
    // BOI[24295535/76561198354029212] was killed by autoturret_deployed (entity)
    // djarcade[21625622/76561197974662404] was killed by Doctor Delete[36356/76561197979952036]
    // jestemVito[18446695/76561198047570527] was killed by guntrap.deployed (entity)
    // torktumlare[24330482/76561198051791695] was killed by landmine (entity)
    // vsra[21298131/76561198084386225] was killed by flameturret_fireball (entity)
    // jeppek02[17783908/76561198052063983] was killed by Doctor Delete[36356/76561197979952036]
    // EPICPLAYZ[18767904/76561198389309935] was killed by bear (Bear)
    // 8938733[16513324/8938733] was killed by Doctor Delete[36356/76561197979952036]

    @Override
    public Function<String, Optional<PlayerDeathEvent>> parseEvent()
    {
        throw new UnsupportedOperationException();
    }
}
