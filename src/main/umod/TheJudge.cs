using System.Collections.Generic;
using System.Linq;
using System;

namespace Oxide.Plugins
{
    [Info("The Judge (Ownzone)", "Ownzone", "0.0.1")]
    [Description("")]
    class TheJudge : RustPlugin
    {
		[ConsoleCommand("judge.hurt")]
		private void JudgeHurt(ConsoleSystem.Arg arg)
		{
			if (arg.Connection != null || (arg.Args == null || arg.Args.Length == 0)) return;

			var player = BasePlayer.FindByID(Convert.ToUInt64(arg.Args[0]));

			if (arg.Args.Length == 1 || arg.Args[1] == null || string.IsNullOrEmpty(arg.Args[1])) {
                Puts("Remember to specify a hurt amount!");
                return;
            }

			if (player != null)
			{
				player.Hurt(Convert.ToInt32(arg.Args[1]));
				Effect.server.Run("assets/bundled/prefabs/fx/screen_jump.prefab", player.transform.position);
				Effect.server.Run("assets/bundled/prefabs/fx/player/gutshot_scream.prefab", player.transform.position);
				Effect.server.Run("assets/bundled/prefabs/fx/impacts/blunt/flesh/fleshbloodimpact.prefab", player.transform.position);
			}
		}

		[ConsoleCommand("judge.bleed")]
		private void JudgeBleed(ConsoleSystem.Arg arg)
		{
			if (arg.Connection != null || (arg.Args == null || arg.Args.Length == 0)) return;

			var player = BasePlayer.FindByID(Convert.ToUInt64(arg.Args[0]));

			if (arg.Args.Length == 1 || arg.Args[1] == null || string.IsNullOrEmpty(arg.Args[1])) {
                Puts("Remember to specify a bleed amount!");
                return;
            }

			if (player != null)
			{
				player.metabolism.bleeding.value = Convert.ToInt32(arg.Args[1]);
				Effect.server.Run("assets/bundled/prefabs/fx/screen_jump.prefab", player.transform.position);
				Effect.server.Run("assets/bundled/prefabs/fx/player/gutshot_scream.prefab", player.transform.position);
				Effect.server.Run("assets/bundled/prefabs/fx/impacts/slash/clothflesh/clothflesh1.prefab", player.transform.position);
			}
		}

		[ConsoleCommand("judge.starve")]
		private void JudgeStarve(ConsoleSystem.Arg arg)
		{
			if (arg.Connection != null || (arg.Args == null || arg.Args.Length == 0)) return;

			var player = BasePlayer.FindByID(Convert.ToUInt64(arg.Args[0]));

			if (player != null)
			{
				player.metabolism.calories.value = 0;
				player.metabolism.hydration.value = 0;
				Effect.server.Run("assets/bundled/prefabs/fx/screen_jump.prefab", player.transform.position);
				Effect.server.Run("assets/bundled/prefabs/fx/gestures/drink_vomit.prefab", player.transform.position);
			}
		}

		[ConsoleCommand("judge.suffocate")]
		private void JudgeSuffocate(ConsoleSystem.Arg arg)
		{
			if (arg.Connection != null || (arg.Args == null || arg.Args.Length == 0)) return;

			var player = BasePlayer.FindByID(Convert.ToUInt64(arg.Args[0]));

			if (player != null)
			{
				player.metabolism.oxygen.value = 0;
				Effect.server.Run("assets/bundled/prefabs/fx/screen_jump.prefab", player.transform.position);
				Effect.server.Run("assets/bundled/prefabs/fx/player/gutshot_scream.prefab", player.transform.position);
				Effect.server.Run("assets/prefabs/clothes/diving.tank/effects/scuba_exhale.prefab", player.transform.position);
			}
		}

		[ConsoleCommand("judge.cook")]
		private void JudgeCook(ConsoleSystem.Arg arg)
		{
			if (arg.Connection != null || (arg.Args == null || arg.Args.Length == 0)) return;

			var player = BasePlayer.FindByID(Convert.ToUInt64(arg.Args[0]));

			if (player != null)
			{
				player.metabolism.temperature.value = 100;
				Effect.server.Run("assets/bundled/prefabs/fx/screen_jump.prefab", player.transform.position);
				Effect.server.Run("assets/bundled/prefabs/fx/player/gutshot_scream.prefab", player.transform.position);
				Effect.server.Run("assets/prefabs/weapons/torch/effects/ignite.prefab", player.transform.position);
			}
		}

		[ConsoleCommand("judge.freeze")]
		private void JudgeFreeze(ConsoleSystem.Arg arg)
		{
			if (arg.Connection != null || (arg.Args == null || arg.Args.Length == 0)) return;

			var player = BasePlayer.FindByID(Convert.ToUInt64(arg.Args[0]));

			if (player != null)
			{
				player.metabolism.temperature.value = -100;
				player.metabolism.wetness.value = player.metabolism.wetness.max;
				Effect.server.Run("assets/bundled/prefabs/fx/screen_jump.prefab", player.transform.position);
				Effect.server.Run("assets/bundled/prefabs/fx/player/gutshot_scream.prefab", player.transform.position);
				Effect.server.Run("assets/bundled/prefabs/fx/explosions/water_bomb.prefab", player.transform.position);
			}
		}

		[ConsoleCommand("judge.poison")]
		private void JudgePoison(ConsoleSystem.Arg arg)
		{
			if (arg.Connection != null || (arg.Args == null || arg.Args.Length == 0)) return;

			var player = BasePlayer.FindByID(Convert.ToUInt64(arg.Args[0]));

			if (arg.Args.Length == 1 || arg.Args[1] == null || string.IsNullOrEmpty(arg.Args[1])) {
                Puts("Remember to specify a poison amount!");
                return;
            }

			if (player != null)
			{
				player.metabolism.poison.value = Convert.ToInt32(arg.Args[1]);
				Effect.server.Run("assets/bundled/prefabs/fx/screen_jump.prefab", player.transform.position);
				Effect.server.Run("assets/bundled/prefabs/fx/player/beartrap_scream.prefab", player.transform.position);
			}
		}

		[ConsoleCommand("judge.radiation")]
		private void JudgeRadiation(ConsoleSystem.Arg arg)
		{
			if (arg.Connection != null || (arg.Args == null || arg.Args.Length == 0)) return;

			var player = BasePlayer.FindByID(Convert.ToUInt64(arg.Args[0]));

			if (arg.Args.Length == 1 || arg.Args[1] == null || string.IsNullOrEmpty(arg.Args[1])) {
                Puts("Remember to specify a radiation amount!");
                return;
            }

			if (player != null)
			{
				player.metabolism.radiation_level.value = Convert.ToInt32(arg.Args[1]);
				player.metabolism.radiation_poison.value = Convert.ToInt32(arg.Args[1]);
				Effect.server.Run("assets/bundled/prefabs/fx/screen_jump.prefab", player.transform.position);
				Effect.server.Run("assets/bundled/prefabs/fx/player/beartrap_scream.prefab", player.transform.position);
			}
		}

		[ConsoleCommand("judge.pardon")]
		private void JudgePardon(ConsoleSystem.Arg arg)
		{
			if (arg.Connection != null || (arg.Args == null || arg.Args.Length == 0)) return;

			var player = BasePlayer.FindByID(Convert.ToUInt64(arg.Args[0]));

			if (player != null)
			{
				player.metabolism.bleeding.value = player.metabolism.bleeding.min;
				player.metabolism.calories.value = player.metabolism.calories.max;
				player.metabolism.comfort.value = 0;
				player.metabolism.hydration.value = player.metabolism.hydration.max;
				player.metabolism.oxygen.value = player.metabolism.oxygen.max;
				player.metabolism.poison.value = player.metabolism.poison.min;
				player.metabolism.radiation_level.value = player.metabolism.radiation_level.min;
				player.metabolism.radiation_poison.value = player.metabolism.radiation_poison.min;
				Effect.server.Run("assets/bundled/prefabs/fx/screen_land.prefab", player.transform.position);
			}
		}
    }
}