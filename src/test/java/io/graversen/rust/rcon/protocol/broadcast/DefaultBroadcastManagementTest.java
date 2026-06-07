package io.graversen.rust.rcon.protocol.broadcast;

import io.graversen.rust.rcon.RustRconResponse;
import io.graversen.rust.rcon.protocol.Codec;
import io.graversen.rust.rcon.protocol.RustRconMessage;
import io.graversen.rust.rcon.protocol.util.SteamId64;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultBroadcastManagementTest {
    @Mock
    private Codec codec;

    @Mock
    private RustRconMessage rustRconMessage;

    @Mock
    private RustRconResponse rustRconResponse;

    @InjectMocks
    private DefaultBroadcastManagement defaultBroadcastManagement;

    private final ArgumentCaptor<String> templateCaptor = ArgumentCaptor.forClass(String.class);

    @BeforeEach
    void setUp() {
        when(codec.raw(anyString())).thenReturn(rustRconMessage);
        when(codec.send(rustRconMessage)).thenReturn(CompletableFuture.completedFuture(rustRconResponse));
    }

    @Test
    void broadcast() {
        final var speakerSteamId = SteamId64.parseOrFail("76561197979952036");

        final var response = defaultBroadcastManagement.broadcast("Server restart in 5 minutes", speakerSteamId);

        verify(codec).raw(templateCaptor.capture());
        verify(codec).send(rustRconMessage);
        assertSame(rustRconResponse, response.join());
        assertEquals("broadcast \"Server restart in 5 minutes\" 76561197979952036", templateCaptor.getValue());
    }

    @Test
    void whisper() {
        final var speakerSteamId = SteamId64.parseOrFail("76561197979952036");
        final var targetSteamId = SteamId64.parseOrFail("76561198154164007");

        final var response = defaultBroadcastManagement.whisper("Your base upkeep is low", speakerSteamId, targetSteamId);

        verify(codec).raw(templateCaptor.capture());
        verify(codec).send(rustRconMessage);
        assertSame(rustRconResponse, response.join());
        assertEquals("whisper \"Your base upkeep is low\" 76561197979952036 76561198154164007", templateCaptor.getValue());
    }

    @Test
    void broadcastEscapesQuotedMessage() {
        final var speakerSteamId = SteamId64.parseOrFail("76561197979952036");

        defaultBroadcastManagement.broadcast("Say \"hi\" near C:\\base", speakerSteamId);

        verify(codec).raw(templateCaptor.capture());
        assertEquals("broadcast \"Say \\\"hi\\\" near C:\\\\base\" 76561197979952036", templateCaptor.getValue());
    }
}
