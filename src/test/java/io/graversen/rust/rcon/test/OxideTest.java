package io.graversen.rust.rcon.test;

import io.graversen.rust.rcon.IRconClient;
import io.graversen.rust.rcon.support.OxideSupport;
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
    private OxideSupport oxideSupport;

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
        oxideSupport.grant(() -> "dummy", () -> "1234", "key1.key2");
        verify(rconClient, times(1)).sendRaw(captor.capture());
        assertEquals("oxide.grant user 1234 dummy.key1.key2", captor.getValue());
    }

    @Test
    void test_grantMethod_redundantKey()
    {
        oxideSupport.grant(() -> "dummy", () -> "1234", "dummy.key1.key2");
        verify(rconClient, times(1)).sendRaw(captor.capture());
        assertEquals("oxide.grant user 1234 dummy.key1.key2", captor.getValue());
    }

    @Test
    void test_revokeMethod_simple()
    {
        oxideSupport.revoke(() -> "dummy", () -> "1234", "key1.key2");
        verify(rconClient, times(1)).sendRaw(captor.capture());
        assertEquals("oxide.revoke user 1234 dummy.key1.key2", captor.getValue());
    }

    @Test
    void test_revokeMethod_redundantKey()
    {
        oxideSupport.revoke(() -> "dummy", () -> "1234", "dummy.key1.key2");
        verify(rconClient, times(1)).sendRaw(captor.capture());
        assertEquals("oxide.revoke user 1234 dummy.key1.key2", captor.getValue());
    }
}
