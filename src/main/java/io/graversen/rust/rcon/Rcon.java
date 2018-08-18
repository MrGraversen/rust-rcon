package io.graversen.rust.rcon;

public class Rcon
{
    private final IRconClient rconClient;
    private final InventoryRcon inventoryRcon;

    public Rcon(IRconClient rconClient)
    {
        this.rconClient = rconClient;
        this.inventoryRcon = new InventoryRcon();
    }

    public void kick(String player, String reason)
    {
        final String command = String.format("kick \"%s\" \"%s\"", player, reason);
        rconClient.sendRaw(command);
    }

    public void ban(String player, String reason)
    {
        final String command = String.format("ban \"%s\" \"%s\"", player, reason);
        rconClient.sendRaw(command);
    }

    public void banId(String steamId, String reason)
    {
        final String command = String.format("banid \"%s\" \"%s\"", steamId, reason);
        rconClient.sendRaw(command);
    }

    public InventoryRcon inventory()
    {
        return inventoryRcon;
    }

    private class InventoryRcon
    {
        public void giveTo(String player, String itemShortName, int amount)
        {
            final String command = String.format("inventory.giveto \"%s\" \"%s\" \"%d\"", player, itemShortName, amount);
            rconClient.sendRaw(command);
        }

        public void giveArm(String player, String itemShortName, int amount)
        {
            final String command = String.format("inventory.givearm \"%s\" \"%s\" \"%d\"", player, itemShortName, amount);
            rconClient.sendRaw(command);
        }

        public void giveAll(String itemShortName, int amount)
        {
            final String command = String.format("inventory.giveall \"%s\" \"%d\"", itemShortName, amount);
            rconClient.sendRaw(command);
        }
    }
}
