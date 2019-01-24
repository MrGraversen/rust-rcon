package io.graversen.rust.rcon.protocol;

import io.graversen.rust.rcon.objects.rust.ISteamPlayer;
import io.graversen.rust.rcon.rustclient.IRconClient;

public class EventRcon extends BaseRcon
{
    EventRcon(IRconClient rconClient)
    {
        super(rconClient);
    }

    public RconEntity airDrop()
    {
        return rconEntity("supply.call");
    }

    public RconEntity patrolHelicopter()
    {
        return rconEntity("heli.call");
    }

    public RconEntity patrolHelicopter(ISteamPlayer steamPlayer)
    {
        patrolHelicopter().execute();
        return rconEntity("heli.strafe %s", steamPlayer.getSteamId());
    }

    public RconEntity resetPatrolHelicopterLifetime()
    {
        return setPatrolHelicopterLifetime(15);
    }

    public RconEntity disablePatrolHelicopter()
    {
        return setPatrolHelicopterLifetime(0);
    }

    public RconEntity setPatrolHelicopterLifetime(int lifetimeMinutes)
    {
        return rconEntity("heli.lifetimeminutes %d", lifetimeMinutes);
    }
}
