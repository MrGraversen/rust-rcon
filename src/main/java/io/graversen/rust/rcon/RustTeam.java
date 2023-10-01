package io.graversen.rust.rcon;

import io.graversen.rust.rcon.protocol.util.SteamId64;
import lombok.NonNull;
import lombok.Value;

import java.util.List;

@Value
public class RustTeam {
    @NonNull String id;
    @NonNull List<SteamId64> players;
}
