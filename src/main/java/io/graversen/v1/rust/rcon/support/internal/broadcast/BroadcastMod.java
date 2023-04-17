package io.graversen.v1.rust.rcon.support.internal.broadcast;

import io.graversen.v1.rust.rcon.objects.rust.ISteamPlayer;
import io.graversen.v1.rust.rcon.objects.rust.Player;
import io.graversen.v1.rust.rcon.rustclient.IRconClient;
import io.graversen.v1.rust.rcon.support.BaseModSupport;

public class BroadcastMod extends BaseModSupport
{
    private static final String BROADCAST_ALL_COMMAND = "broadcast_all";
    private static final String BROADCAST_TO_COMMAND = "broadcast_to";
    private static final String BROADCAST_HACK_COMMAND = "broadcast_hack";

    public BroadcastMod(IRconClient rconClient)
    {
        super(rconClient);
    }

    @Override
    public String modName()
    {
        return "Broadcast (Ownzone)";
    }

    @Override
    public String description()
    {
        return "Internally developed mod to aid sending messages to all or a single player";
    }

    @Override
    public String version()
    {
        return "0.0.1";
    }

    @Override
    public String umodLink()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public String umodDirectLink()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean requiresModification()
    {
        return false;
    }

    public void broadcastAll(IMessage message)
    {
        final String command = String.format("%s \"%s\"", BROADCAST_ALL_COMMAND, message.message());
        rconClient().send(command);
    }

    public void broadcastTo(IMessage message, ISteamPlayer steamPlayer)
    {
        final String command = String.format("%s \"%s\" %s", BROADCAST_TO_COMMAND, message.message(), steamPlayer.getSteamId());
        rconClient().send(command);
    }

    public void broadcastHack(IMessage message, Player player)
    {
        final MessageBuilder fullMessage = messageBuilder()
                .color(player.getDisplayName(), "#55AAFF")
                .plain(": ")
                .plain(message.message());

        final String command = String.format("%s \"%s\" %s", BROADCAST_HACK_COMMAND, fullMessage.message(), player.getSteamId());
        rconClient().send(command);
    }

    public static MessageBuilder messageBuilder()
    {
        return new MessageBuilder();
    }

    public static class MessageBuilder implements IMessage
    {
        private final StringBuilder messageBuilder;

        MessageBuilder()
        {
            this.messageBuilder = new StringBuilder();
        }

        public MessageBuilder plain(String text)
        {
            this.messageBuilder.append(text);
            return this;
        }

        public MessageBuilder newLine()
        {
            this.messageBuilder.append("<br>");
            return this;
        }

        public MessageBuilder indent()
        {
            this.messageBuilder.append(" \t ");
            return this;
        }

        public MessageBuilder italic(String text)
        {
            this.messageBuilder.append("<i>").append(text).append("</i>");
            return this;
        }

        public MessageBuilder underline(String text)
        {
            this.messageBuilder.append("<u>").append(text).append("</u>");
            return this;
        }

        public MessageBuilder color(String text, String colorHex)
        {
            if (!colorHex.startsWith("#"))
            {
                colorHex = "#" + colorHex;
            }

            this.messageBuilder.append("<color=").append(colorHex).append(">").append(text).append("</color>");
            return this;
        }

        public MessageBuilder size(String text, int size)
        {
            this.messageBuilder.append("<size=").append(size).append(">").append(text).append("</size>");
            return this;
        }

        @Override
        public String message()
        {
            return messageBuilder.toString();
        }
    }
}
