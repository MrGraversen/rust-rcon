package io.graversen.rust.rcon.events.implementation;

import io.graversen.rust.rcon.events.IEventParser;
import io.graversen.rust.rcon.events.types.custom.PlayerDeathEvent;
import io.graversen.rust.rcon.serialization.DefaultSerializer;
import io.graversen.trunk.io.serialization.interfaces.ISerializer;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;

public class PlayerDeathEventParser implements IEventParser<PlayerDeathEvent>
{
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
            rconMessage = rconMessage.substring(10).trim();

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

    private class PlayerDeathPayload
    {
        private final String victim;
        private final String killer;
        private final String bodypart;
        private final BigDecimal distance;
        private final Integer hp;
        private final String weapon;
        private final String attachments;

        public PlayerDeathPayload(
                String victim,
                String killer,
                String bodypart,
                BigDecimal distance,
                Integer hp,
                String weapon,
                String attachments
        )
        {
            this.victim = victim;
            this.killer = killer;
            this.bodypart = bodypart;
            this.distance = distance;
            this.hp = hp;
            this.weapon = weapon;
            this.attachments = attachments;
        }

        private PlayerDeathEvent toPlayerDeathEvent()
        {
            final String[] attachments = this.attachments == null
                    ? new String[0]
                    : Arrays.stream(this.attachments.split(",")).map(String::trim).toArray(String[]::new);

            return new PlayerDeathEvent(
                    this.victim,
                    this.killer,
                    this.bodypart,
                    this.distance,
                    this.hp,
                    this.weapon,
                    attachments
            );
        }
    }
}
