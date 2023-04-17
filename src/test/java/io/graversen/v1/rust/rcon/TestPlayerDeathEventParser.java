package io.graversen.v1.rust.rcon;

import io.graversen.v1.rust.rcon.events.IEventParser;
import io.graversen.v1.rust.rcon.events.implementation.PlayerDeathEventParser;
import io.graversen.v1.rust.rcon.events.types.custom.PlayerDeathEvent;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TestPlayerDeathEventParser extends BaseDefaultParserTest
{
    private final String[] messages = {
            "[Undertaker (Ownzone)] {\"victim\":\"Bear\",\"killer\":\"Doctor Delete\",\"bodypart\":\"Chest\",\"distance\":\"4.02\",\"hp\":\"100\",\"weapon\":\"MP5A4\",\"attachments\":\"Silencer, Weapon Lasersight, Holosight\"}",
            "[Undertaker (Ownzone)] {\"victim\":\"szok99\",\"killer\":\"Doctor Delete\",\"bodypart\":\"Head\",\"distance\":\"0.74\",\"hp\":\"100\",\"weapon\":\"MP5A4\",\"attachments\":\"Silencer, Weapon Lasersight, Holosight\"}",
            "[Undertaker (Ownzone)] {\"victim\":\"SOSA\",\"killer\":\"Gun Trap\",\"bodypart\":\"Body\",\"distance\":\"1.66\",\"owner\":\"Doctor Delete\"}",
            "[Undertaker (Ownzone)] {\"victim\":\"soares pt\",\"killer\":\"Doctor Delete\",\"bodypart\":\"Stomach\",\"distance\":\"3.79\",\"hp\":\"100\",\"weapon\":\"M249\",\"attachments\":\"Weapon flashlight, Holosight, Silencer\"}",
            "[Undertaker (Ownzone)] {\"victim\":\"Scientist\",\"killer\":\"Doctor Delete\",\"bodypart\":\"Head\",\"distance\":\"118.36\",\"hp\":\"100\",\"weapon\":\"L96 Rifle\",\"attachments\":\"Weapon Lasersight, 8x Zoom Scope, Silencer\"}",
            "[Undertaker (Ownzone)] {\"victim\":\"Josma\",\"killer\":\"Bear\",\"bodypart\":\"Body\",\"distance\":\"0.00\"}",
            "[Undertaker (Ownzone)] {\"victim\":\"Qwerty\",\"killer\":\"Flame Turret\",\"bodypart\":\"Body\",\"distance\":\"1.07\",\"owner\":\"Doctor Delete\"}",
            "[Undertaker (Ownzone)] {\"victim\":\"Fakorr\",\"killer\":\"Auto Turret\",\"bodypart\":\"Body\",\"distance\":\"2.97\",\"owner\":\"Doctor Delete\"}",
            "[Undertaker (Ownzone)] {\"victim\":\"Larz\",\"killer\":\"Landmine\",\"bodypart\":\"Body\",\"distance\":\"0.87\",\"owner\":\"Doctor Delete\"}",
            "[Undertaker (Ownzone)] {\"victim\":\"Rino_Shikarnyi\"}"
    };

    private final IEventParser<PlayerDeathEvent> eventParser = new PlayerDeathEventParser();

    @Test
    void test_validation()
    {
        Arrays.stream(messages).map(eventParser.parseEvent()).forEach(event -> assertTrue(event.isPresent()));
    }

    @Test
    void test_findCorrectInfo_1()
    {
        final Optional<PlayerDeathEvent> event = eventParser.parseEvent().apply(messages[0]);

        assertTrue(event.isPresent());
        assertEquals("Bear", event.get().getVictim());
        assertEquals("Doctor Delete", event.get().getKiller());
        assertEquals("Chest", event.get().getBodyPart());
        assertEquals(new BigDecimal("4.02"), event.get().getDistance());
        assertEquals(Integer.valueOf(100), event.get().getHp());
        assertEquals("MP5A4", event.get().getWeapon());
        assertEquals(3, event.get().getAttachments().length);
    }

    @Test
    void test_findCorrectInfo_2()
    {
        final Optional<PlayerDeathEvent> event = eventParser.parseEvent().apply(messages[2]);

        assertTrue(event.isPresent());
        assertEquals("SOSA", event.get().getVictim());
        assertEquals("Doctor Delete", event.get().getKiller());
        assertEquals("Body", event.get().getBodyPart());
        assertEquals(new BigDecimal("1.66"), event.get().getDistance());
        assertNull(event.get().getHp());
        assertEquals("Gun Trap", event.get().getWeapon());
        assertEquals(0, event.get().getAttachments().length);
    }

    @Test
    void test_findCorrectInfo_3()
    {
        final Optional<PlayerDeathEvent> event = eventParser.parseEvent().apply(messages[5]);

        assertTrue(event.isPresent());
        assertEquals("Josma", event.get().getVictim());
        assertEquals("Bear", event.get().getKiller());
        assertEquals("Body", event.get().getBodyPart());
        assertEquals(new BigDecimal("0.00"), event.get().getDistance());
        assertNull(event.get().getHp());
        assertNull(event.get().getWeapon());
        assertEquals(0, event.get().getAttachments().length);
    }
}
