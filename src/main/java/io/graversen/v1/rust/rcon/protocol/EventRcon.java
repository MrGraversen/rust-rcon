package io.graversen.v1.rust.rcon.protocol;

import io.graversen.v1.rust.rcon.objects.rust.ISteamPlayer;
import io.graversen.v1.rust.rcon.rustclient.IRconClient;

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
