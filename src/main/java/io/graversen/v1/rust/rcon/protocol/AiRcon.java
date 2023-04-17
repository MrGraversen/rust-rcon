package io.graversen.v1.rust.rcon.protocol;

import io.graversen.v1.rust.rcon.rustclient.IRconClient;

public class AiRcon extends BaseRcon
{
    AiRcon(IRconClient rconClient)
    {
        super(rconClient);
    }

    public RconEntity think(boolean think)
    {
        return rconEntity("ai.think %s", String.valueOf(think));
    }

    public RconEntity move(boolean move)
    {
        return rconEntity("ai.move %s", String.valueOf(move));
    }
}
