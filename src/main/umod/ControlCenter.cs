using System.Collections.Generic;
using System.Linq;
using System;

namespace Oxide.Plugins
{
    [Info("ControlCenter (Ownzone)", "Ownzone", "0.0.1")]
    [Description("Exposes controls for various events of the server")]
    class ControlCenter : RustPlugin
    {
        [ConsoleCommand("cc.chinook")]
        private void CmdSpawnChinook(ConsoleSystem.Arg arg)
		{
			var ch47 = (CH47HelicopterAIController) GameManager.server.CreateEntity("assets/prefabs/npc/ch47/ch47scientists.entity.prefab", new Vector3(0, 200, 0));
            if (ch47 == null) return;
            ch47.Spawn();
		}

		[ConsoleCommand("cc.patrolheli")]
        private void CmdSpawnPatrolHeli(ConsoleSystem.Arg arg)
		{
            var heli = GameManager.server.CreateEntity("assets/prefabs/npc/patrol helicopter/patrolhelicopter.prefab");
            if (heli == null) return;
            heli.Spawn();
		}

		[ConsoleCommand("cc.airdrop")]
        private void CmdSpawnPatrolHeli(ConsoleSystem.Arg arg)
		{
            var entity = GameManager.server.CreateEntity("assets/prefabs/npc/cargo plane/cargo_plane.prefab", new Vector3());
            entity?.Spawn();
		}
    }
}