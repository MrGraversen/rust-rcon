package io.graversen.rust.rcon.events.implementation;

import io.graversen.rust.rcon.events.IEventParser;
import io.graversen.rust.rcon.events.types.custom.PlayerDeathEvent;
import io.graversen.rust.rcon.objects.util.DamageTypes;
import io.graversen.rust.rcon.objects.util.DeathTypes;
import io.graversen.rust.rcon.serialization.DefaultSerializer;
import io.graversen.trunk.io.serialization.interfaces.ISerializer;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class PlayerDeathEventParser implements IEventParser<PlayerDeathEvent>
{
    public static final String MESSAGE_PREFIX = "[Undertaker (Ownzone)]";
    private final ISerializer serializer;

    public PlayerDeathEventParser()
    {
        this.serializer = new DefaultSerializer();
    }

    @Override
    public Function<String, Optional<PlayerDeathEvent>> parseEvent()
    {
        return rconMessage ->
        {
            rconMessage = rconMessage.substring(MESSAGE_PREFIX.length()).trim();

            try
            {
                final PlayerDeathPayload playerDeathPayload = serializer.deserialize(rconMessage, PlayerDeathPayload.class);
                return Optional.of(playerDeathPayload.toPlayerDeathEvent());
            }
            catch (Exception e)
            {
                return Optional.empty();
            }
        };
    }

    static class PlayerDeathPayload
    {
        private String victim;
        private String killer;
        private String bodypart;
        private String distance;
        private String hp;
        private String weapon;
        private String attachments;
        private String owner;
        private String damageType;
        private String killerEntityType;
        private String victimEntityType;

        public PlayerDeathPayload(
                String victim,
                String killer,
                String bodypart,
                String distance,
                String hp,
                String weapon,
                String attachments,
                String owner,
                String damageType,
                String killerEntityType,
                String victimEntityType
        )
        {
            this.victim = victim;
            this.killer = killer;
            this.bodypart = bodypart;
            this.distance = distance;
            this.hp = hp;
            this.weapon = weapon;
            this.attachments = attachments;
            this.owner = owner;
            this.damageType = damageType;
            this.killerEntityType = killerEntityType;
            this.victimEntityType = victimEntityType;
        }

        private PlayerDeathEvent toPlayerDeathEvent()
        {
            final String[] attachments = this.attachments == null
                    ? new String[0]
                    : Arrays.stream(this.attachments.split(",")).map(String::trim).toArray(String[]::new);

            if (Objects.nonNull(owner))
            {
                this.weapon = this.killer;
                this.killer = this.owner;
            }

            final DeathTypes deathType = DeathTypes.resolve(killerEntityType, victimEntityType);
            final DamageTypes damageType = DamageTypes.parse(this.damageType);

            this.distance = this.distance.replaceAll("[^\\d.]", "");

            return new PlayerDeathEvent(
                    this.victim,
                    this.killer,
                    this.bodypart,
                    new BigDecimal(this.distance),
                    new BigDecimal(this.hp),
                    this.weapon,
                    attachments,
                    deathType,
                    damageType
            );
        }
    }
}
