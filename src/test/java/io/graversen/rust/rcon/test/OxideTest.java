package io.graversen.rust.rcon.test;

import io.graversen.rust.rcon.rustclient.IRconClient;
import io.graversen.rust.rcon.support.UmodSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class OxideTest
{
    @Mock
    private IRconClient rconClient;

    @InjectMocks
    private UmodSupport umodSupport;

    @Captor
    private ArgumentCaptor<String> captor;

    @BeforeEach
    void setUp()
    {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void test_grantMethod_simple()
    {
        umodSupport.grant(() -> "dummy", () -> "1234", "key1.key2");
        verify(rconClient, times(1)).send(captor.capture());
        assertEquals("oxide.grant user 1234 dummy.key1.key2", captor.getValue());
    }

    @Test
    void test_grantMethod_redundantKey()
    {
        umodSupport.grant(() -> "dummy", () -> "1234", "dummy.key1.key2");
        verify(rconClient, times(1)).send(captor.capture());
        assertEquals("oxide.grant user 1234 dummy.key1.key2", captor.getValue());
    }

    @Test
    void test_revokeMethod_simple()
    {
        umodSupport.revoke(() -> "dummy", () -> "1234", "key1.key2");
        verify(rconClient, times(1)).send(captor.capture());
        assertEquals("oxide.revoke user 1234 dummy.key1.key2", captor.getValue());
    }

    @Test
    void test_revokeMethod_redundantKey()
    {
        umodSupport.revoke(() -> "dummy", () -> "1234", "dummy.key1.key2");
        verify(rconClient, times(1)).send(captor.capture());
        assertEquals("oxide.revoke user 1234 dummy.key1.key2", captor.getValue());
    }
}
