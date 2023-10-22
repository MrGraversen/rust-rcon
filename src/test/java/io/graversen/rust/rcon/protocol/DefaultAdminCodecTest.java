package io.graversen.rust.rcon.protocol;

import io.graversen.rust.rcon.RustRconRouter;
import io.graversen.rust.rcon.protocol.util.PlayerName;
import io.graversen.rust.rcon.protocol.util.SteamId64;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultAdminCodecTest {
    @Mock
    private RustRconRouter rustRconRouter;

    @InjectMocks
    private DefaultAdminCodec defaultAdminCodec;

    @Captor
    private ArgumentCaptor<RustRconMessage> rconMessageCaptor;

    @Test
    void banPlayer() {
        final var steamId = SteamId64.parseOrFail("76561197979952036");
        final var playerName = new PlayerName("Doctor Delete");
        final var reason = "You are banned m8";
        defaultAdminCodec.banPlayer(steamId, playerName, reason);

        verify(rustRconRouter).send(rconMessageCaptor.capture());
        assertEquals("global.banid \"76561197979952036\" \"Doctor Delete\" \"You are banned m8\"", rconMessageCaptor.getValue().get());
    }

    @Test
    void kickPlayer() {
        final var steamId = SteamId64.parseOrFail("76561197979952036");
        final var reason = "You are kicked m8";
        defaultAdminCodec.kickPlayer(steamId, reason);

        verify(rustRconRouter).send(rconMessageCaptor.capture());
        assertEquals("global.kick \"76561197979952036\" \"You are kicked m8\"", rconMessageCaptor.getValue().get());
    }
}