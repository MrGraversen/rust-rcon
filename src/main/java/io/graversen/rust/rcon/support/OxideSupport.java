package io.graversen.rust.rcon.support;

import io.graversen.rust.rcon.objects.rust.ISteamPlayer;
import io.graversen.rust.rcon.rustclient.IRconClient;

import java.util.Arrays;

public class OxideSupport
{
    private static final String OXIDE_GRANT = "oxide.grant";
    private static final String OXIDE_REVOKE = "oxide.revoke";
    private static final String OXIDE_RELOAD = "oxide.reload";

    private final IRconClient rconClient;

    public OxideSupport(IRconClient rconClient)
    {
        this.rconClient = rconClient;
    }

    public void grant(IOxidePermissible oxidePermissible, ISteamPlayer player, String permission)
    {
        permission = sanitizePermissionString(oxidePermissible, permission);
        final String command = getCommandString(oxidePermissible, OXIDE_GRANT, player, permission);

        rconClient.send(command);
    }

    public void revoke(IOxidePermissible oxidePermissible, ISteamPlayer player, String permission)
    {
        permission = sanitizePermissionString(oxidePermissible, permission);
        final String command = getCommandString(oxidePermissible, OXIDE_REVOKE, player, permission);

        rconClient.send(command);
    }

    public void reloadAll()
    {
        reload("*");
    }

    public void reload(String pluginName)
    {
        final String command = String.format("%s %s", OXIDE_RELOAD, pluginName);
    }

    private String getCommandString(IOxidePermissible oxidePermissible, String oxidePrefix, ISteamPlayer player, String permission)
    {
        final String permissionString = String.format("%s.%s", oxidePermissible.baseName(), permission);
        return String.format("%s user %s %s", oxidePrefix, player.getSteamId(), permissionString);
    }

    private String sanitizePermissionString(IOxidePermissible oxidePermissible, String permission)
    {
        if (permission.startsWith(oxidePermissible.baseName()))
        {
            final String[] permissionKeys = permission.split("\\.");
            permission = String.join(".", Arrays.copyOfRange(permissionKeys, 1, permissionKeys.length));
        }

        return permission;
    }
}
