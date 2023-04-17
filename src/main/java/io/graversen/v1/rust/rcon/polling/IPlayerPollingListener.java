package io.graversen.v1.rust.rcon.polling;

import io.graversen.v1.rust.rcon.objects.rust.Player;

import java.util.List;

@FunctionalInterface
public interface IPlayerPollingListener
{
    void onResult(List<Player> players);
}
