package io.graversen.rust.rcon.protocol;

public class Rcon
{
    private final AiRcon aiRcon;
    private final EventRcon eventRcon;
    private final InventoryRcon inventoryRcon;
    private final SettingsRcon settingsRcon;

    public Rcon()
    {
        this.aiRcon = new AiRcon();
        this.eventRcon = new EventRcon();
        this.inventoryRcon = new InventoryRcon();
        this.settingsRcon = new SettingsRcon();
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
