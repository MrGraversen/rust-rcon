package io.graversen.rust.rcon.polling;

import io.graversen.rust.rcon.objects.rust.Player;

import java.util.List;

@FunctionalInterface
public interface IPlayerPollingListener
{
    void onResult(List<Player> players);
}
