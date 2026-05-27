using System;
using System.Collections.Generic;
using Newtonsoft.Json;

namespace Oxide.Plugins
{
    [Info("RustRconBridge", "Ownzone", "0.0.1")]
    [Description("Emits structured Rust RCON bridge events for rust-rcon Java clients")]
    class RustRconBridge : RustPlugin
    {
        private const int SchemaVersion = 1;
        private const string Prefix = "[rust-rcon]";

        private object OnPlayerChat(BasePlayer player, string message, Chat.ChatChannel channel)
        {
            Emit("player.chat", new Dictionary<string, object>
            {
                ["steamId"] = player.UserIDString,
                ["playerName"] = player.displayName,
                ["message"] = message,
                ["chatChannel"] = ChatChannelName(channel)
            });

            return null;
        }

        private void OnPlayerConnected(BasePlayer player)
        {
            Emit("player.connected", new Dictionary<string, object>
            {
                ["steamId"] = player.UserIDString,
                ["playerName"] = player.displayName,
                ["ipAddress"] = IpAddress(player)
            });
        }

        private void OnPlayerDisconnected(BasePlayer player, string reason)
        {
            Emit("player.disconnected", new Dictionary<string, object>
            {
                ["steamId"] = player.UserIDString,
                ["playerName"] = player.displayName,
                ["reason"] = reason
            });
        }

        private void Emit(string eventType, object payload)
        {
            var envelope = new Dictionary<string, object>
            {
                ["schemaVersion"] = SchemaVersion,
                ["eventType"] = eventType,
                ["eventId"] = Guid.NewGuid().ToString("N"),
                ["timestamp"] = DateTime.UtcNow.ToString("o"),
                ["payload"] = payload
            };

            Puts($"{Prefix} {JsonConvert.SerializeObject(envelope)}");
        }

        private string ChatChannelName(Chat.ChatChannel channel)
        {
            return string.Equals(channel.ToString(), "Team", StringComparison.OrdinalIgnoreCase)
                ? "team"
                : "default";
        }

        private string IpAddress(BasePlayer player)
        {
            return player?.net?.connection?.ipaddress ?? string.Empty;
        }
    }
}
