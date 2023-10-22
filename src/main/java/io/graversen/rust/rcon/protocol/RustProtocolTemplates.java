package io.graversen.rust.rcon.protocol;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

import static io.graversen.rust.rcon.protocol.RustProtocolTemplates.Placeholders.*;
import static io.graversen.rust.rcon.protocol.RustProtocolTemplates.Placeholders.OxidePlaceholders.*;

@UtilityClass
public class RustProtocolTemplates {
    public static class Placeholders {
        public static final String STEAM_ID_64 = "${steamId64}";
        public static final String PLAYER_NAME = "${playerName}";
        public static final String MINUTES = "${minutes}";
        public static final String ENABLED = "${enabled}";
        public static final String AMOUNT = "${amount}";
        public static final String ANIMAL = "${animal}";
        public static final String VEHICLE = "${vehicle}";
        public static final String REASON = "${reason}";

        public static class OxidePlaceholders {
            public static final String PERMISSION_TYPE = "${permissionType}";
            public static final String NAME = "${name}";
            public static final String PERMISSION = "${permission}";
        }

        public static String stripped(@NonNull String placeholder) {
            return placeholder.substring(2, placeholder.length() - 1);
        }
    }

    public static class EventProtocol {
        public static final String CALL_AIR_DROP = "supply.call";
        public static final String CALL_PATROL_HELICOPTER = "heli.call";
        public static final String STRAFE_PATROL_HELICOPTER = "heli.strafe " + STEAM_ID_64;
        public static final String PATROL_HELICOPTER_LIFETIME = "heli.lifetimeminutes " + MINUTES;
    }

    public static class SettingsProtocol {
        public static final String DECAY_SCALE = "decay.scale " + AMOUNT;
        public static final String DECAY_UPKEEP_ENABLED = "decay.upkeep " + ENABLED;
        public static final String STABILITY_ENABLED = "server.stability " + ENABLED;
        public static final String RADIATION_ENABLED = "server.radiation " + ENABLED;
        public static final String GLOBAL_CHAT_ENABLED = "server.globalchat " + ENABLED;
        public static final String ANIMAL_POPULATION = ANIMAL + ".population " + AMOUNT;
        public static final String VEHICLE_POPULATION = VEHICLE + ".population " + AMOUNT;
    }

    public static class AdminProtocol {
        public static final String KICK_PLAYER = "global.kick \"" + STEAM_ID_64 + "\" \"" + REASON + "\"";
        public static final String KICK_ALL_PLAYERS = "global.kickall";
        public static final String BAN_PLAYER = "global.banid \"" + STEAM_ID_64 + "\" \"" + PLAYER_NAME + "\" \"" + REASON + "\"";
        public static final String UNBAN_PLAYER = "global.unban \"" + STEAM_ID_64 + "\"";
        public static final String ADD_OWNER = "global.ownerid \"" + STEAM_ID_64 + "\" \"" + PLAYER_NAME + "\"";
        public static final String REMOVE_OWNER = "global.removeowner \"" + STEAM_ID_64 + "\"";
        public static final String MUTE_PLAYER = "global.mute \"" + STEAM_ID_64 + "\"";
        public static final String UNMUTE_PLAYER = "global.unmute \"" + STEAM_ID_64 + "\"";
        public static final String SERVER_INFO = "global.serverinfo";
        public static final String PLAYER_LIST = "playerlist";
        public static final String SLEEPING_PLAYERS = "global.sleepingusers";
        public static final String TEAM_INFO = "global.teaminfo \"" + STEAM_ID_64 + "\"";
    }

    public static class OxideProtocol {
        public static final String PLUGINS = "oxide.plugins";
        public static final String GRANT = "oxide.grant " + PERMISSION_TYPE + " " + NAME + " " + PERMISSION;
        public static final String REVOKE = "oxide.revoke " + PERMISSION_TYPE + " " + NAME + " " + PERMISSION;
    }
}
