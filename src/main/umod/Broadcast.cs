using System.Collections.Generic;
using System.Linq;
using System;

namespace Oxide.Plugins
{
    [Info("Broadcast (Ownzone)", "Ownzone", "0.0.1")]
    [Description("Broadcast a message to the server, or to a player")]
    class Broadcast : RustPlugin
    {
        readonly ulong ChatIcon = 1337;

        [ConsoleCommand("broadcast_id")]
        private void BroadcastHack(ConsoleSystem.Arg arg) {
            if (arg.Connection != null || (arg.Args == null || arg.Args.Length == 0)) return;

            if (arg.Args.Length == 1 || arg.Args[1] == null || string.IsNullOrEmpty(arg.Args[1])) {
                Puts("Remember to specify a target player icon (SteamID64)!");
                return;
            }

            Puts($"Broadcasting message \"{arg.Args[0]}\" to all players, impersonating SteamID \"{arg.Args[1]}\"");
            Server.Broadcast(arg.Args[0], ulong.Parse(arg.Args[1]));
        }

        [ConsoleCommand("broadcast_to")]
        private void BroadcastTo(ConsoleSystem.Arg arg) {
            if (arg.Connection != null || arg.Args == null) return;

            if (arg.Args.Length == 1 || arg.Args[1] == null || string.IsNullOrEmpty(arg.Args[1])) {
                Puts("Remember to specify a target player (SteamID64)!");
                return;
            }

            var player = BasePlayer.FindByID(Convert.ToUInt64(arg.Args[1]));

            if (player != null) {
                Puts($"Sending message \"{arg.Args[0]}\" to player with id \"{arg.Args[1]}\"");
                Player.Message(player, arg.Args[0], null, ChatIcon);
            } else {
                Puts($"Not sending message to player with id \"{arg.Args[1]}\" because they are not online");
            }
        }
    }
}