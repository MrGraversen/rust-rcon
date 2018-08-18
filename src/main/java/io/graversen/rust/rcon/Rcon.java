package io.graversen.rust.rcon;

public class Rcon
{
    private final IRconClient rconClient;

    private final InventoryRcon inventoryRcon;
    private final AiRcon aiRcon;

    public Rcon(IRconClient rconClient)
    {
        this.rconClient = rconClient;
        this.inventoryRcon = new InventoryRcon();
        this.aiRcon = new AiRcon();
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

    public void ownerId(String steamId, String name, String reason)
    {
        final String command = String.format("ownerid \"%s\" \"%s\" \"%s\"", steamId, name, reason);
        rconClient.sendRaw(command);
    }

    public void removeOwner(String steamId)
    {
        final String command = String.format("removeowner \"%s\"", steamId);
        rconClient.sendRaw(command);
    }

    public InventoryRcon inventory()
    {
        return inventoryRcon;
    }

    public AiRcon ai()
    {
        return aiRcon;
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

    private class AiRcon
    {
        public void think(boolean think)
        {
            final String command = String.format("ai.think %s", String.valueOf(think));
            rconClient.sendRaw(command);
        }

        public void move(boolean move)
        {
            final String command = String.format("ai.move %s", String.valueOf(move));
            rconClient.sendRaw(command);
        }
    }
}
