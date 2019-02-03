package io.graversen.rust.rcon.events.types.custom;

import io.graversen.rust.rcon.events.types.BaseRustEvent;

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

    // TODO: Eventually indicate if event is PVP, PVE, or trap

    public PlayerDeathEvent(String victim, String killer, String bodyPart, BigDecimal distance, Integer hp, String weapon, String[] attachments)
    {
        this.victim = victim;
        this.killer = killer;
        this.bodyPart = bodyPart;
        this.distance = distance;
        this.hp = hp;
        this.weapon = weapon;
        this.attachments = attachments;
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
}
