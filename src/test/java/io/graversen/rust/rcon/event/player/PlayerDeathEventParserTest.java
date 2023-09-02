package io.graversen.rust.rcon.event.player;

import io.graversen.rust.rcon.TestRustRconResponse;
import io.graversen.rust.rcon.protocol.util.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PlayerDeathEventParserTest {
    private static final String KNOWN_PAYLOAD_1 = "[Undertaker (Ownzone)] {\"victim\":\"Bear\",\"killer\":\"Doctor Delete\",\"bodypart\":\"Body\",\"distance\":\"27.4 meters\",\"hp\":\"100\",\"weapon\":\"Assault Rifle\",\"attachments\":\"Weapon flashlight, Extended Magazine, Muzzle Brake\",\"killerId\":\"76561197979952036\",\"victimId\":null,\"damageType\":\"Bullet\",\"killerEntityType\":\"Player\",\"victimEntityType\":\"Animal\"}";
    private static final String KNOWN_PAYLOAD_2 = "[Undertaker (Ownzone)] {\"victim\":\"8660607\",\"killer\":\"Doctor Delete\",\"bodypart\":\"Head\",\"distance\":\"7 meters\",\"hp\":\"100\",\"weapon\":\"Assault Rifle\",\"attachments\":\"Weapon flashlight, Extended Magazine, Muzzle Brake\",\"killerId\":\"76561197979952036\",\"victimId\":\"8660607\",\"damageType\":\"Bullet\",\"killerEntityType\":\"Player\",\"victimEntityType\":\"Player\"}";
    private static final String KNOWN_PAYLOAD_3 = "[Undertaker (Ownzone)] {\"victim\":\"Scientist\",\"killer\":\"PenetrationsKonsulten\",\"bodypart\":\"Stomach\",\"distance\":\"1.7 meters\",\"hp\":\"86.8\",\"weapon\":\"HMLMG\",\"attachments\":\"Holosight, Weapon flashlight\",\"killerId\":\"76561198075300933\",\"victimId\":\"8769831\",\"damageType\":\"Bullet\",\"killerEntityType\":\"Player\",\"victimEntityType\":\"Scientist\"}";
    private static final String KNOWN_PAYLOAD_4 = "[Undertaker (Ownzone)] {\"victim\":\"BP123\",\"killer\":\"Scientist\",\"bodypart\":\"Body\",\"distance\":\"3.7 meters\",\"killerId\":\"7271119\",\"victimId\":\"76561198813879070\",\"damageType\":\"Bullet\",\"killerEntityType\":\"Scientist\",\"victimEntityType\":\"Player\"}";
    private final PlayerDeathEventParser playerDeathEventParser = new PlayerDeathEventParser();

    @Test
    void parseEvent_knownPayload1() {
        final var playerDeathEvent = playerDeathEventParser.parseEvent(new TestRustRconResponse(KNOWN_PAYLOAD_1));
        assertNotNull(playerDeathEvent);
        assertTrue(playerDeathEvent.isPresent());
        assertEquals("Bear", playerDeathEvent.get().getVictim());
        assertEquals("Doctor Delete", playerDeathEvent.get().getKiller());
        assertEquals(BodyParts.BODY, playerDeathEvent.get().getBodyPart());
        assertEquals(new BigDecimal("27.4"), playerDeathEvent.get().getDistance());
        assertEquals(new BigDecimal("100"), playerDeathEvent.get().getKillerHealth());
        assertEquals("Assault Rifle", playerDeathEvent.get().getWeapon());
        assertEquals(Set.of("Weapon flashlight", "Extended Magazine", "Muzzle Brake"), playerDeathEvent.get().getAttachments());
        assertEquals(new SteamId64("76561197979952036"), playerDeathEvent.get().getSteamId());
        assertNull(playerDeathEvent.get().getVictimId());
        assertEquals(CombatTypes.PVE, playerDeathEvent.get().getCombatType());
        assertEquals(DamageTypes.BULLET, playerDeathEvent.get().getDamageType());
        assertEquals(EntityTypes.PLAYER, playerDeathEvent.get().getKillerEntity());
        assertEquals(EntityTypes.ANIMAL, playerDeathEvent.get().getVictimEntity());
    }

    @Test
    void parseEvent_knownPayload2() {
        final var playerDeathEvent = playerDeathEventParser.parseEvent(new TestRustRconResponse(KNOWN_PAYLOAD_2));
        assertNotNull(playerDeathEvent);
        assertTrue(playerDeathEvent.isPresent());
        assertEquals("8660607", playerDeathEvent.get().getVictim());
        assertEquals("Doctor Delete", playerDeathEvent.get().getKiller());
        assertEquals(BodyParts.HEAD, playerDeathEvent.get().getBodyPart());
        assertEquals(new BigDecimal("7"), playerDeathEvent.get().getDistance());
        assertEquals(new BigDecimal("100"), playerDeathEvent.get().getKillerHealth());
        assertEquals("Assault Rifle", playerDeathEvent.get().getWeapon());
        assertEquals(Set.of("Weapon flashlight", "Extended Magazine", "Muzzle Brake"), playerDeathEvent.get().getAttachments());
        assertEquals(new SteamId64("76561197979952036"), playerDeathEvent.get().getSteamId());
        assertNull(playerDeathEvent.get().getVictimId());
        assertEquals(CombatTypes.PVP, playerDeathEvent.get().getCombatType());
        assertEquals(DamageTypes.BULLET, playerDeathEvent.get().getDamageType());
        assertEquals(EntityTypes.PLAYER, playerDeathEvent.get().getKillerEntity());
        assertEquals(EntityTypes.PLAYER, playerDeathEvent.get().getVictimEntity());
    }

    @Test
    void parseEvent_knownPayload3() {
        final var playerDeathEvent = playerDeathEventParser.parseEvent(new TestRustRconResponse(KNOWN_PAYLOAD_3));
        assertNotNull(playerDeathEvent);
        assertTrue(playerDeathEvent.isPresent());
        assertEquals("Scientist", playerDeathEvent.get().getVictim());
        assertEquals("PenetrationsKonsulten", playerDeathEvent.get().getKiller());
        assertEquals(BodyParts.STOMACH, playerDeathEvent.get().getBodyPart());
        assertEquals(new BigDecimal("1.7"), playerDeathEvent.get().getDistance());
        assertEquals(new BigDecimal("86.8"), playerDeathEvent.get().getKillerHealth());
        assertEquals("HMLMG", playerDeathEvent.get().getWeapon());
        assertEquals(Set.of("Weapon flashlight", "Holosight"), playerDeathEvent.get().getAttachments());
        assertEquals(new SteamId64("76561198075300933"), playerDeathEvent.get().getSteamId());
        assertNull(playerDeathEvent.get().getVictimId());
        assertEquals(CombatTypes.PVE, playerDeathEvent.get().getCombatType());
        assertEquals(DamageTypes.BULLET, playerDeathEvent.get().getDamageType());
        assertEquals(EntityTypes.PLAYER, playerDeathEvent.get().getKillerEntity());
        assertEquals(EntityTypes.SCIENTIST, playerDeathEvent.get().getVictimEntity());
    }

    @Test
    void parseEvent_knownPayload4() {
        final var playerDeathEvent = playerDeathEventParser.parseEvent(new TestRustRconResponse(KNOWN_PAYLOAD_4));
        assertNotNull(playerDeathEvent);
        assertTrue(playerDeathEvent.isPresent());
        assertEquals("BP123", playerDeathEvent.get().getVictim());
        assertEquals("Scientist", playerDeathEvent.get().getKiller());
        assertEquals(BodyParts.BODY, playerDeathEvent.get().getBodyPart());
        assertEquals(new BigDecimal("3.7"), playerDeathEvent.get().getDistance());
        assertEquals(BigDecimal.ZERO, playerDeathEvent.get().getKillerHealth());
        assertNull(playerDeathEvent.get().getWeapon());
        assertEquals(Set.of(), playerDeathEvent.get().getAttachments());
        assertEquals(new SteamId64("76561198813879070"), playerDeathEvent.get().getSteamId());
        assertEquals(new SteamId64("76561198813879070"), playerDeathEvent.get().getVictimId());
        assertEquals(CombatTypes.PVE, playerDeathEvent.get().getCombatType());
        assertEquals(DamageTypes.BULLET, playerDeathEvent.get().getDamageType());
        assertEquals(EntityTypes.SCIENTIST, playerDeathEvent.get().getKillerEntity());
        assertEquals(EntityTypes.PLAYER, playerDeathEvent.get().getVictimEntity());
    }
}