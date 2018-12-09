package io.graversen.rust.rcon.composite;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Loadout implements Iterable<Item>
{
    private final String playerName;
    private final List<Item> items;

    public Loadout(String playerName)
    {
        this(playerName, new ArrayList<>());
    }

    public Loadout(String playerName, List<Item> items)
    {
        this.playerName = playerName;
        this.items = items;
    }

    public String getPlayerName()
    {
        return playerName;
    }

    @Override
    public Iterator<Item> iterator()
    {
        return items.iterator();
    }
}
