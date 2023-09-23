package io.graversen.rust.rcon.protocol.oxide;

import io.graversen.rust.rcon.TestRustRconResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DefaultOxideManagementTest {
    @Mock
    private OxideCodec oxideCodec;

    @InjectMocks
    private DefaultOxideManagement defaultOxideManagement;

    @Test
    void mapOxidePlugins() {
        final var oxidePluginOutput = "Listing 13 plugins:\n" +
                "  01 \"Welcomer\" (2.1.0) by Dana (0.16s) - Welcomer.cs\n" +
                "  02 \"Vanish\" (1.6.6) by Whispers88 (0.02s) - Vanish.cs\n" +
                "  03 \"Undertaker (Ownzone)\" (0.0.1) by Ownzone (0.00s) - Undertaker.cs\n" +
                "  04 \"Stack Size Controller\" (4.1.2) by AnExiledDev/patched by chrome (54.42s) - StackSizeController.cs\n" +
                "  05 \"SmoothRestarter\" (3.2.0) by 2CHEVSKII (0.03s) - SmoothRestarter.cs\n" +
                "  06 \"No Green\" (1.3.10) by Iv Misticos (0.01s) - NoGreen.cs\n" +
                "  07 \"No Give Notices\" (0.3.0) by Wulf (0.00s) - NoGiveNotices.cs\n" +
                "  08 \"Gather Manager\" (2.2.78) by Mughisi (0.27s) - GatherManager.cs\n" +
                "  09 \"Enhanced Hammer\" (2.1.1) by misticos (2.70s) - EnhancedHammer.cs\n" +
                "  10 \"Death Notes\" (6.3.9) by LaserHydra/Mevent/Ownzone (0.24s) - DeathNotes.cs\n" +
                "  11 \"Blueprint Manager\" (2.0.6) by Whispers88 (0.02s) - BlueprintManager.cs\n" +
                "  12 \"Admin Radar\" (5.3.2) by nivex (0.44s) - AdminRadar.cs\n" +
                "  13 \"AdminHammer\" (1.13.0) by mvrb (0.00s) - AdminHammer.cs\n";

        final var plugins = defaultOxideManagement.mapOxidePlugins().apply(new TestRustRconResponse(oxidePluginOutput));
        assertEquals(13, plugins.size());
        assertEquals("Stack Size Controller", plugins.get(3).getPluginName());
        assertEquals("4.1.2", plugins.get(3).getPluginVersion());
        assertEquals("AnExiledDev/patched by chrome", plugins.get(3).getPluginAuthor());
        assertEquals("StackSizeController.cs", plugins.get(3).getPluginFile());
    }
}