package io.graversen.rust.rcon.protocol;

import io.graversen.rust.rcon.rustclient.IRconClient;

public class Rcon extends BaseRcon
{
    private final AiRcon aiRcon;
    private final EventRcon eventRcon;
    private final InventoryRcon inventoryRcon;
    private final SettingsRcon settingsRcon;

    public Rcon(IRconClient rconClient)
    {
        super(rconClient);
        this.aiRcon = new AiRcon(rconClient);
        this.eventRcon = new EventRcon(rconClient);
        this.inventoryRcon = new InventoryRcon(rconClient);
        this.settingsRcon = new SettingsRcon(rconClient);
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
}
