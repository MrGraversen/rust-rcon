using System.Collections.Generic;
using System.Linq;
using System;
using UnityEngine;
using Newtonsoft.Json;

namespace Oxide.Plugins
{
    [Info("ControlCenter (Ownzone)", "Ownzone", "0.0.1")]
    [Description("Exposes controls for various events of the server")]
    class ControlCenter : RustPlugin
    {
        [ConsoleCommand("cc.chinook")]
        private void CmdSpawnChinook(ConsoleSystem.Arg arg)
		{
			var ch47 = (CH47HelicopterAIController) GameManager.server.CreateEntity("assets/prefabs/npc/ch47/ch47scientists.entity.prefab", RandomLocation());
            if (ch47 == null) return;
            ch47.Spawn();
		}

		[ConsoleCommand("cc.patrolheli")]
        private void CmdSpawnPatrolHeli(ConsoleSystem.Arg arg)
		{
			var heli = GameManager.server.CreateEntity("assets/prefabs/npc/patrol helicopter/patrolhelicopter.prefab", RandomLocation());
            if (heli == null) return;
            heli.Spawn();
		}

		[ConsoleCommand("cc.patrolheli_to")]
        private void CmdSpawnPatrolHeliTo(ConsoleSystem.Arg arg)
		{
			if (arg.Connection != null || arg.Args == null) return;

            if (arg.Args.Length == 0 || arg.Args[0] == null || string.IsNullOrEmpty(arg.Args[0])) {
                Puts("Remember to specify a target player (SteamID64)!");
                return;
            }

			var player = BasePlayer.FindByID(Convert.ToUInt64(arg.Args[0]));
			var playerPosition = player.transform.position;

			var heli = (BaseHelicopter) GameManager.server.CreateEntity(
				"assets/prefabs/npc/patrol helicopter/patrolhelicopter.prefab",
				playerPosition,
				new Quaternion(),
				true
			);

			Puts(JsonConvert.SerializeObject(playerPosition));

            if (heli == null) return;
            heli.Spawn();
		}


		[ConsoleCommand("cc.airdrop")]
        private void CmdSpawnAirDrop(ConsoleSystem.Arg arg)
		{
            var airdrop = GameManager.server.CreateEntity("assets/prefabs/npc/cargo plane/cargo_plane.prefab", new Vector3());
			if (airdrop == null) return;
            airdrop.Spawn();
		}

		private Vector3 RandomLocation()
		{
			var worldSize = ConVar.Server.worldsize;
			return new Vector3(
				Oxide.Core.Random.Range(-worldSize^2/2, worldSize^2/2),
				300,
				Oxide.Core.Random.Range(-worldSize^2/2, worldSize^2/2)
			);

		}
    }
}