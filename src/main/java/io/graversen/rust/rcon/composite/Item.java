package io.graversen.rust.rcon.composite;

public class Item
{
    private final String shortName;
    private final int amount;

    public Item(String shortName, int amount)
    {
        this.shortName = shortName;
        this.amount = amount;
    }

    public String getShortName()
    {
        return shortName;
    }

    public int getAmount()
    {
        return amount;
    }
}
