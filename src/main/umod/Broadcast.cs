using System.Collections.Generic;
using System.Linq;
using System;

namespace Oxide.Plugins
{
    [Info("Broadcast", "Martin Graversen", "0.0.1", ResourceId = 1)]
    [Description("Broadcast a message to the server")]
    class Broadcast : RustPlugin
    {
        [ConsoleCommand("broadcast")]
        private void cmdConsoleBroadcast(ConsoleSystem.Arg arg) {
            if (arg.Connection != null || (arg.Args == null || arg.Args.Length == 0)) return;
            PrintToChat(arg.Args[0]);
        }

        [ConsoleCommand("broadcastto")]
        private void cmdConsoleBroadcastTo(ConsoleSystem.Arg arg) {
            if (arg.Connection != null || (arg.Args == null || arg.Args.Length == 0)) return;

            var player = BasePlayer.FindByID(Convert.ToUInt64(arg.Args[0]));

            if (player != null) {
                PrintToChat(player, arg.Args[1]);
            }
        }
    }
}