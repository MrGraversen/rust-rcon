package io.graversen.rust.rcon.events.types.custom;

import io.graversen.rust.rcon.events.types.BaseRustEvent;
import io.graversen.rust.rcon.objects.util.DeathTypes;

import java.math.BigDecimal;

public class PlayerDeathEvent extends BaseRustEvent
{
    private final String victim;
    private final String killer;
    private final String bodyPart;
    private final BigDecimal distance;
    private final Integer hp;
    private final String weapon;
    private final String[] attachments;
    private final DeathTypes deathType;
    private final String damageType;

    public PlayerDeathEvent(
            String victim,
            String killer,
            String bodyPart,
            BigDecimal distance,
            Integer hp,
            String weapon,
            String[] attachments,
            DeathTypes deathType,
            String damageType
    )
    {
        this.victim = victim;
        this.killer = killer;
        this.bodyPart = bodyPart;
        this.distance = distance;
        this.hp = hp;
        this.weapon = weapon;
        this.attachments = attachments;
        this.deathType = deathType;
        this.damageType = damageType;
    }

    public String getVictim()
    {
        return victim;
    }

    public String getKiller()
    {
        return killer;
    }

    public String getBodyPart()
    {
        return bodyPart;
    }

    public BigDecimal getDistance()
    {
        return distance;
    }

    public Integer getHp()
    {
        return hp;
    }

    public String getWeapon()
    {
        return weapon;
    }

    public String[] getAttachments()
    {
        return attachments;
    }

    public DeathTypes getDeathType()
    {
        return deathType;
    }

    public String getDamageType()
    {
        return damageType;
    }
}
