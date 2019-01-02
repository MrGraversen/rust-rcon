using System.Collections.Generic;
using System.Linq;
using System;

namespace Oxide.Plugins
{
    [Info("Broadcast", "Martin Graversen", "0.0.1", ResourceId = 1)]
    [Description("Broadcast a message to the server")]
    class EasyBroadcast : RustPlugin
    {
        [ConsoleCommand("broadcast")]
        void broadcast(ConsoleSystem.Arg arg) {
            if (arg.Connection != null || (arg.Args == null || arg.Args.Length == 0)) return;

            // TODO!
        }
    }
}