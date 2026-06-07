namespace Oxide.Plugins
{
    [Info("Broadcast (Ownzone)", "Ownzone", "0.0.2")]
    [Description("Sends broadcast and private chat messages with a supplied SteamID64 avatar")]
    class Broadcast : RustPlugin
    {
        private const string WhisperPrefix = "<color=#8f8f8f>[whisper]</color> ";

        [ConsoleCommand("broadcast")]
        private void BroadcastMessage(ConsoleSystem.Arg arg)
        {
            if (!IsServerConsole(arg))
            {
                return;
            }

            if (!HasArgumentCount(arg, 2, "broadcast \"<message>\" <speakerSteamId>"))
            {
                return;
            }

            if (!TryParseSteamId(arg.Args[1], "speakerSteamId", out var speakerSteamId))
            {
                return;
            }

            var message = arg.Args[0];
            Puts($"Broadcasting message \"{message}\" to all players as SteamID \"{speakerSteamId}\"");
            Server.Broadcast(message, null, speakerSteamId);
        }

        [ConsoleCommand("whisper")]
        private void WhisperMessage(ConsoleSystem.Arg arg)
        {
            if (!IsServerConsole(arg))
            {
                return;
            }

            if (!HasArgumentCount(arg, 3, "whisper \"<message>\" <speakerSteamId> <targetSteamId>"))
            {
                return;
            }

            if (!TryParseSteamId(arg.Args[1], "speakerSteamId", out var speakerSteamId) ||
                !TryParseSteamId(arg.Args[2], "targetSteamId", out var targetSteamId))
            {
                return;
            }

            var player = BasePlayer.FindByID(targetSteamId);
            if (player == null)
            {
                Puts($"Not sending whisper to SteamID \"{targetSteamId}\" because they are not online");
                return;
            }

            var message = FormatWhisper(arg.Args[0]);
            Puts($"Sending whisper \"{arg.Args[0]}\" to SteamID \"{targetSteamId}\" as SteamID \"{speakerSteamId}\"");
            Player.Message(player, message, null, speakerSteamId);
        }

        private bool IsServerConsole(ConsoleSystem.Arg arg)
        {
            return arg.Connection == null;
        }

        private bool HasArgumentCount(ConsoleSystem.Arg arg, int expected, string usage)
        {
            if (arg.Args != null && arg.Args.Length == expected)
            {
                return true;
            }

            Puts($"Usage: {usage}");
            return false;
        }

        private bool TryParseSteamId(string value, string argumentName, out ulong steamId)
        {
            if (ulong.TryParse(value, out steamId))
            {
                return true;
            }

            Puts($"Invalid {argumentName}: \"{value}\". Expected a SteamID64.");
            return false;
        }

        private string FormatWhisper(string message)
        {
            return $"{WhisperPrefix}{message}";
        }
    }
}
