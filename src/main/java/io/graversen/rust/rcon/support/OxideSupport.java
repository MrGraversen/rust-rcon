package io.graversen.rust.rcon.support;

import io.graversen.rust.rcon.IRconClient;
import io.graversen.rust.rcon.objects.rust.Player;

import java.util.Arrays;

public class OxideSupport
{
    private final String OXIDE_GRANT = "oxide.grant";
    private final String OXIDE_REVOKE = "oxide.revoke";
    private final IRconClient rconClient;

    public OxideSupport(IRconClient rconClient)
    {
        this.rconClient = rconClient;
    }

    public void grant(IOxidePermissible oxidePermissible, Player player, String permission)
    {
        permission = sanitizePermissionString(oxidePermissible, permission);
        final String command = getCommandString(oxidePermissible, OXIDE_GRANT, player, permission);

        rconClient.sendRaw(command);
    }

    public void revoke(IOxidePermissible oxidePermissible, Player player, String permission)
    {
        permission = sanitizePermissionString(oxidePermissible, permission);
        final String command = getCommandString(oxidePermissible, OXIDE_REVOKE, player, permission);

        rconClient.sendRaw(command);
    }

    private String getCommandString(IOxidePermissible oxidePermissible, String oxidePrefix, Player player, String permission)
    {
        final String permissionString = String.format("%s.%s", oxidePermissible.baseName(), permission);
        return String.format("%s user %s %s", oxidePrefix, player.getSteamId(), permissionString);
    }

    private String sanitizePermissionString(IOxidePermissible oxidePermissible, String permission)
    {
        if (permission.startsWith(oxidePermissible.baseName()))
        {
            final String[] permissionKeys = permission.split(".");
            permission = String.join(".", Arrays.copyOfRange(permissionKeys, 1, permission.length()));
        }

        return permission;
    }
}
