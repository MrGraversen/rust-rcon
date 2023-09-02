package io.graversen.rust.rcon.protocol;

import io.graversen.rust.rcon.RustRconResponse;
import io.graversen.rust.rcon.RustRconRouter;
import io.graversen.rust.rcon.protocol.util.Animals;
import io.graversen.rust.rcon.protocol.util.Vehicles;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static io.graversen.rust.rcon.protocol.RustProtocolTemplates.Placeholders.*;
import static io.graversen.rust.rcon.protocol.RustProtocolTemplates.SettingsProtocol.*;

public class DefaultSettingsCodec extends DefaultRustCodec implements SettingsCodec {
    public DefaultSettingsCodec(@NonNull RustRconRouter rustRconRouter) {
        super(rustRconRouter);
    }

    @Override
    public CompletableFuture<RustRconResponse> decayScale(@NonNull BigDecimal amount) {
        final var rconMessage = compile(
                DECAY_SCALE,
                Map.of(
                        stripped(AMOUNT), amount.toString()
                )
        );
        return send(rconMessage);
    }

    @Override
    public CompletableFuture<RustRconResponse> decayUpkeepEnabled(@NonNull Boolean enabled) {
        final var rconMessage = compile(
                DECAY_UPKEEP_ENABLED,
                Map.of(
                        stripped(ENABLED), String.valueOf(enabled)
                )
        );
        return send(rconMessage);
    }

    @Override
    public CompletableFuture<RustRconResponse> stabilityEnabled(@NonNull Boolean enabled) {
        final var rconMessage = compile(
                STABILITY_ENABLED,
                Map.of(
                        stripped(ENABLED), String.valueOf(enabled)
                )
        );
        return send(rconMessage);
    }

    @Override
    public CompletableFuture<RustRconResponse> radiationEnabled(@NonNull Boolean enabled) {
        final var rconMessage = compile(
                RADIATION_ENABLED,
                Map.of(
                        stripped(ENABLED), String.valueOf(enabled)
                )
        );
        return send(rconMessage);
    }

    @Override
    public CompletableFuture<RustRconResponse> globalChatEnabled(@NonNull Boolean enabled) {
        final var rconMessage = compile(
                GLOBAL_CHAT_ENABLED,
                Map.of(
                        stripped(ENABLED), String.valueOf(enabled)
                )
        );
        return send(rconMessage);
    }

    @Override
    public CompletableFuture<RustRconResponse> animalPopulation(@NonNull Animals animal, BigDecimal amount) {
        final var rconMessage = compile(
                ANIMAL_POPULATION,
                Map.of(
                        stripped(ANIMAL), animal.name().toLowerCase(),
                        stripped(AMOUNT), amount.toString()
                )
        );
        return send(rconMessage);
    }

    @Override
    public CompletableFuture<RustRconResponse> vehiclePopulation(@NonNull Vehicles vehicle, BigDecimal amount) {
        final var rconMessage = compile(
                VEHICLE_POPULATION,
                Map.of(
                        stripped(ANIMAL), vehicle.name().toLowerCase(),
                        stripped(AMOUNT), amount.toString()
                )
        );
        return send(rconMessage);
    }
}
