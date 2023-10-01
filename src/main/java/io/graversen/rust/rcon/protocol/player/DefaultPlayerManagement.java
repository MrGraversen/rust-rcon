package io.graversen.rust.rcon.protocol.player;

import io.graversen.rust.rcon.FullRustPlayer;
import io.graversen.rust.rcon.RustPlayer;
import io.graversen.rust.rcon.RustRconResponse;
import io.graversen.rust.rcon.RustTeam;
import io.graversen.rust.rcon.protocol.AdminCodec;
import io.graversen.rust.rcon.protocol.dto.RustDtoMappers;
import io.graversen.rust.rcon.protocol.dto.RustPlayerDTO;
import io.graversen.rust.rcon.protocol.util.PlayerName;
import io.graversen.rust.rcon.protocol.util.SteamId64;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
public class DefaultPlayerManagement implements PlayerManagement {
    private final @NonNull AdminCodec adminCodec;
    private final @NonNull RustDtoMappers rustDtoMappers;

    @Override
    public CompletableFuture<List<FullRustPlayer>> players() {
        return adminCodec.playerList()
                .thenApply(rustDtoMappers.mapRustPlayers())
                .thenApply(mapRustPlayers());
    }

    @Override
    public CompletableFuture<List<RustPlayer>> sleepingPlayers() {
        return adminCodec.sleepingPlayers().thenApply(mapSleepingPlayers());
    }

    @Override
    public CompletableFuture<Optional<RustTeam>> team(@NonNull SteamId64 steamId64) {
        return adminCodec.teamInfo(steamId64).thenApply(mapRustTeam());
    }

    @Override
    public CompletableFuture<List<RustTeam>> getTeams() {
        return sleepingPlayers()
                .thenCombine(players(), combinePlayerLists())
                .thenCompose(resolveTeams())
                .thenApply(deduplicateRustTeams());
    }

    Function<List<RustTeam>, List<RustTeam>> deduplicateRustTeams() {
        return rustTeams -> {
            final var deduplicatedRustTeams = rustTeams.stream()
                    .collect(Collectors.toUnmodifiableMap(RustTeam::getId, rustTeam -> rustTeam, (existing, replacement) -> existing));

            return List.copyOf(deduplicatedRustTeams.values());
        };
    }

    Function<List<RustPlayer>, CompletableFuture<List<RustTeam>>> resolveTeams() {
        return rustPlayers -> {
            final var resolvingTeams = rustPlayers.stream()
                    .map(resolveTeam())
                    .toList();

            return CompletableFuture.allOf(resolvingTeams.toArray(new CompletableFuture[0]))
                    .thenApply(ignored -> resolvingTeams.stream()
                            .map(CompletableFuture::join)
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .toList()
                    );
        };
    }

    Function<RustPlayer, CompletableFuture<Optional<RustTeam>>> resolveTeam() {
        return rustPlayer -> team(rustPlayer.getSteamId());
    }

    Function<RustRconResponse, Optional<RustTeam>> mapRustTeam() {
        return rconResponse -> {
            try {
                final var message = rconResponse.getMessage();

                if ("Player not found".equalsIgnoreCase(message)) {
                    return Optional.empty();
                }

                final var lines = Arrays.stream(message.split("\n"))
                        .map(String::trim)
                        .filter(string -> !string.isBlank())
                        .toList();

                String teamId = null;
                final var steamIds = new ArrayList<String>();

                for (final var line : lines) {
                    if (teamId == null) {
                        Matcher idMatcher = Pattern.compile("^ID:\\s+(\\d+)$").matcher(line);
                        if (idMatcher.matches()) {
                            teamId = idMatcher.group(1);
                        }
                    } else {
                        Matcher steamIdMatcher = Pattern.compile("^(\\d{17})").matcher(line);
                        if (steamIdMatcher.find()) {
                            steamIds.add(steamIdMatcher.group());
                        }
                    }
                }

                if (teamId != null) {
                    final var mappedSteamIds = steamIds.stream()
                            .map(SteamId64::parseOrFail)
                            .toList();

                    return Optional.of(new RustTeam(teamId, mappedSteamIds));
                }

                return Optional.empty();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return Optional.empty();
            }
        };
    }

    Function<RustRconResponse, List<RustPlayer>> mapSleepingPlayers() {
        return rconResponse -> {
            try {
                final var message = rconResponse.getMessage();
                return Arrays.stream(message.split("\n"))
                        .filter(line -> !line.isBlank())
                        .filter(line -> !line.equalsIgnoreCase("<slot:userid:\"name\">"))
                        .filter(line -> !line.contains("sleeping users"))
                        .map(line -> line.split(":"))
                        .map(parts -> new RustPlayer(SteamId64.parseOrFail(parts[0].trim()), new PlayerName(parts[1].trim())))
                        .toList();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return List.of();
            }
        };
    }

    Function<List<RustPlayerDTO>, List<FullRustPlayer>> mapRustPlayers() {
        return rustPlayerDTOs -> rustPlayerDTOs.stream().map(mapRustPlayer()).toList();
    }

    Function<RustPlayerDTO, FullRustPlayer> mapRustPlayer() {
        return rustPlayerDTO -> new FullRustPlayer(
                SteamId64.parseOrFail(rustPlayerDTO.getSteamId()),
                new PlayerName(rustPlayerDTO.getPlayerName()),
                rustPlayerDTO.getPing(),
                rustPlayerDTO.getIpAddress().split(":")[0],
                Duration.ofSeconds(rustPlayerDTO.getConnectedSeconds()),
                BigDecimal.valueOf(rustPlayerDTO.getHealth())
        );
    }

    private BiFunction<List<RustPlayer>, List<FullRustPlayer>, List<RustPlayer>> combinePlayerLists() {
        return (rustPlayers, fullRustPlayers) -> Stream.concat(rustPlayers.stream(), fullRustPlayers.stream()).distinct().toList();
    }
}
