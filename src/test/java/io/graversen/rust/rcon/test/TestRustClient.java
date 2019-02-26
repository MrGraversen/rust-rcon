package io.graversen.rust.rcon.test;

import io.graversen.fiber.event.bus.IEventBus;
import io.graversen.fiber.event.common.IEvent;
import io.graversen.rust.rcon.RconException;
import io.graversen.rust.rcon.events.types.server.RconErrorEvent;
import io.graversen.rust.rcon.events.types.server.RconMessageEvent;
import io.graversen.rust.rcon.rustclient.RustClient;
import io.graversen.rust.rcon.websocket.IWebSocketClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TestRustClient
{
    @Mock
    private IWebSocketClient webSocketClient;

    @Mock
    private IEventBus eventBus;

    @Captor
    private ArgumentCaptor<Class<? extends IEvent>> eventCaptor;

    @BeforeEach
    void setUp()
    {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void test_openFlow()
    {
        final RustClient rustClient = RustClient.builder().withEventBus(eventBus).build(webSocketClient);

        rustClient.open();

        assertTrue(rustClient.isOpen());
        verify(eventBus, times(1)).start();
        verify(webSocketClient, times(1)).open();

        verify(eventBus, times(2)).registerEventListener(eventCaptor.capture(), any(Supplier.class));
        assertEquals(RconMessageEvent.class, eventCaptor.getAllValues().get(0));
        assertEquals(RconErrorEvent.class, eventCaptor.getAllValues().get(1));
    }

    @Test
    void test_openThenOpen()
    {
        final RustClient rustClient = RustClient.builder().withEventBus(eventBus).build(webSocketClient);

        rustClient.open();

        assertThrows(RconException.class, rustClient::open);
    }

    @Test
    void test_sendBeforeOpen()
    {
        final RustClient rustClient = RustClient.builder().connectTo("x", "x").build();
        assertThrows(RconException.class, () -> rustClient.send("Hello World"));
    }

    @Test
    void test_builder_validations()
    {
        assertThrows(NullPointerException.class, () -> RustClient.builder().withRconMessageParser(null));

        assertThrows(NullPointerException.class, () -> RustClient.builder().withEventBus(null));

        assertThrows(NullPointerException.class, () -> RustClient.builder().withWebSocketListener(null));

        assertThrows(NullPointerException.class, () -> RustClient.builder().withSerializer(null));

        assertThrows(NullPointerException.class, () -> RustClient.builder().withLogger(null));

        assertThrows(NullPointerException.class, () -> RustClient.builder().connectTo(null, "x"));
        assertThrows(NullPointerException.class, () -> RustClient.builder().connectTo("x", null));
        assertThrows(IllegalArgumentException.class, () -> RustClient.builder().connectTo("x", "x", 1));
        assertThrows(IllegalArgumentException.class, () -> RustClient.builder().connectTo("x", "x", 100000));
    }
}
