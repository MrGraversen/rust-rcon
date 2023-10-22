package io.graversen.rust.rcon.protocol.player;

import io.graversen.rust.rcon.RustPlayer;
import io.graversen.rust.rcon.TestRustRconResponse;
import io.graversen.rust.rcon.protocol.AdminCodec;
import io.graversen.rust.rcon.protocol.dto.RustDtoMappers;
import io.graversen.rust.rcon.protocol.util.PlayerName;
import io.graversen.rust.rcon.protocol.util.SteamId64;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DefaultPlayerManagementTest {
    @Mock
    private AdminCodec adminCodec;

    @Mock
    private RustDtoMappers rustDtoMappers;

    @InjectMocks
    private DefaultPlayerManagement defaultPlayerManagement;

    @Test
    void mapSleepingPlayers() {
        final var sleepingPlayersOutput = "\n" +
                "76561198127947780:DKautobahnTV\n" +
                "76561198813879070:MrCool88\n" +
                "76561198072271313:Trey\n" +
                "76561198203638647:cuzzy\n" +
                "76561198088326077:Hirabayashi\n" +
                "76561198436002452:Arska\n" +
                "76561198973297741:Nikke\n" +
                "76561198251899848:Klarius\n" +
                "76561198307063094:IX\n" +
                "76561198046357656:DarkDouchebag\n" +
                "76561198075300933:[RTA]PenetrationsKonsulten\n" +
                "76561198154164007:Hubbi3000\n" +
                "76561198201936914:Bosthief\n" +
                "13 sleeping users\n";

        final var sleepingPlayers = defaultPlayerManagement.mapSleepingPlayers().apply(new TestRustRconResponse(sleepingPlayersOutput));
        assertFalse(sleepingPlayers.isEmpty());
        assertEquals(13, sleepingPlayers.size());
        assertEquals(new RustPlayer(SteamId64.parseOrFail("76561198127947780"), new PlayerName("DKautobahnTV")), sleepingPlayers.get(0));
        assertEquals(new RustPlayer(SteamId64.parseOrFail("76561198201936914"), new PlayerName("Bosthief")), sleepingPlayers.get(12));
    }

    @Test
    void mapRustTeam() {
        final var teamInfoOutput = "ID: 2\n" +
                "\n" +
                "steamID           username      online leader \n" +
                "76561197979952036 Doctor Delete x      x      \n" +
                "76561198154164007 Hubbi3000                   \n" +
                "76561198046357656 DarkDouchebag               \n";

        final var rustTeams = defaultPlayerManagement.mapRustTeam().apply(new TestRustRconResponse(teamInfoOutput));
        assertTrue(rustTeams.isPresent());
        assertEquals(3, rustTeams.get().getPlayers().size());
        assertEquals("2", rustTeams.get().getId());
        assertEquals(SteamId64.parseOrFail("76561197979952036"), rustTeams.get().getPlayers().get(0));
        assertEquals(SteamId64.parseOrFail("76561198154164007"), rustTeams.get().getPlayers().get(1));
        assertEquals(SteamId64.parseOrFail("76561198046357656"), rustTeams.get().getPlayers().get(2));
    }

    @Test
    void mapRustTeam_noTeam() {
        final var teamInfoOutput = "Player not found";

        final var rustTeams = defaultPlayerManagement.mapRustTeam().apply(new TestRustRconResponse(teamInfoOutput));
        assertFalse(rustTeams.isPresent());
    }
}