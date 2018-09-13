package io.graversen.rust.rcon.composite;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Loadout
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

    public Stream<Item> getItems()
    {
        return items.stream();
    }
}
