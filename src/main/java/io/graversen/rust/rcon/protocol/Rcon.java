package io.graversen.rust.rcon.protocol;

import io.graversen.rust.rcon.objects.rust.ISteamPlayer;
import io.graversen.rust.rcon.rustclient.IRconClient;
import io.graversen.trunk.io.serialization.interfaces.ISerializer;

public class Rcon extends BaseRcon
{
    private final AiRcon aiRcon;
    private final EventRcon eventRcon;
    private final InventoryRcon inventoryRcon;
    private final SettingsRcon settingsRcon;
    private final InfoRcon infoRcon;

    public Rcon(IRconClient rconClient, ISerializer serializer)
    {
        super(rconClient);
        this.aiRcon = new AiRcon(rconClient);
        this.eventRcon = new EventRcon(rconClient);
        this.inventoryRcon = new InventoryRcon(rconClient);
        this.settingsRcon = new SettingsRcon(rconClient);
        this.infoRcon = new InfoRcon(rconClient, serializer);
    }

    public AiRcon ai()
    {
        return aiRcon;
    }

    public EventRcon event()
    {
        return eventRcon;
    }

    public InventoryRcon inventory()
    {
        return inventoryRcon;
    }

    public SettingsRcon settings()
    {
        return settingsRcon;
    }

    public InfoRcon info()
    {
        return infoRcon;
    }

    public RconEntity writeConfig()
    {
        return rconEntity("server.writecfg");
    }

    public RconEntity readCfg()
    {
        return rconEntity("server.readcfg");
    }

    public RconEntity muteChat(ISteamPlayer steamPlayer)
    {
        return rconEntity("global.mutechat \"%s\"", steamPlayer.getSteamId());
    }

    public RconEntity muteVoice(ISteamPlayer steamPlayer)
    {
        return rconEntity("global.mutevoice \"%s\"", steamPlayer.getSteamId());
    }

    public RconEntity unmuteChat(ISteamPlayer steamPlayer)
    {
        return rconEntity("global.unmutechat \"%s\"", steamPlayer.getSteamId());
    }

    public RconEntity unmuteVoice(ISteamPlayer steamPlayer)
    {
        return rconEntity("global.unmutevoice \"%s\"", steamPlayer.getSteamId());
    }

    public RconEntity kick(ISteamPlayer steamPlayer)
    {
        return kick(steamPlayer, "");
    }

    public RconEntity kick(ISteamPlayer steamPlayer, String reason)
    {
        return rconEntity("global.kick \"%s\" \"%s\"", steamPlayer.getSteamId(), reason);
    }

    public RconEntity kickAll()
    {
        return rconEntity("global.kickall");
    }

    public RconEntity ban(ISteamPlayer steamPlayer)
    {
        return ban(steamPlayer, "");
    }

    public RconEntity ban(ISteamPlayer steamPlayer, String reason)
    {
        return rconEntity("global.banid \"%s\" \"%s\"", steamPlayer.getSteamId(), reason);
    }

    public RconEntity unban(ISteamPlayer steamPlayer)
    {
        return rconEntity("global.unban \"%s\"", steamPlayer.getSteamId());
    }

    public RconEntity deleteAllEntities(ISteamPlayer steamPlayer)
    {
        return rconEntity("entity.deleteby \"%s\"", steamPlayer.getSteamId());
    }

    public RconEntity addOwner(ISteamPlayer steamPlayer, String name, String reason)
    {
        return rconEntity("global.ownerid \"%s\" \"%s\" \"%s\"", steamPlayer.getSteamId(), name, reason);
    }

    public RconEntity removeOwner(ISteamPlayer steamPlayer)
    {
        return rconEntity("global.removeowner \"%s\"", steamPlayer.getSteamId());
    }
}
