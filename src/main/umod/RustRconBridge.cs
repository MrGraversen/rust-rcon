using System;
using System.Collections.Generic;
using Newtonsoft.Json;
using Oxide.Core.Libraries.Covalence;

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

        private void OnServerInitialized()
        {
            Emit("server.initialized", new Dictionary<string, object>());
        }

        private void OnServerSave()
        {
            Emit("server.save", new Dictionary<string, object>());
        }

        private void OnServerShutdown()
        {
            Emit("server.shutdown", new Dictionary<string, object>());
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

        private void OnUserKicked(IPlayer player, string reason)
        {
            Emit("player.kicked", new Dictionary<string, object>
            {
                ["steamId"] = player?.Id ?? string.Empty,
                ["playerName"] = player?.Name ?? string.Empty,
                ["ipAddress"] = player?.Address ?? string.Empty,
                ["reason"] = reason ?? string.Empty
            });
        }

        private void OnUserBanned(string playerName, string playerId, string ipAddress, string reason, long expiry)
        {
            Emit("player.banned", new Dictionary<string, object>
            {
                ["steamId"] = playerId ?? string.Empty,
                ["playerName"] = playerName ?? string.Empty,
                ["ipAddress"] = ipAddress ?? string.Empty,
                ["reason"] = reason ?? string.Empty,
                ["expiry"] = expiry
            });
        }

        private void OnUserUnbanned(string playerName, string playerId, string ipAddress)
        {
            Emit("player.unbanned", new Dictionary<string, object>
            {
                ["steamId"] = playerId ?? string.Empty,
                ["playerName"] = playerName ?? string.Empty,
                ["ipAddress"] = ipAddress ?? string.Empty
            });
        }

        private void OnPlayerRespawned(BasePlayer player)
        {
            EmitPlayerLifecycle("player.respawned", player);
        }

        private object OnPlayerWound(BasePlayer player, HitInfo info)
        {
            EmitPlayerLifecycle("player.wounded", player);
            return null;
        }

        private object OnPlayerRecover(BasePlayer player)
        {
            EmitPlayerLifecycle("player.recovered", player);
            return null;
        }

        private void OnRawDeathNotice(Dictionary<string, string> data, string message)
        {
            Emit("player.death", data);
        }

        private void EmitPlayerLifecycle(string eventType, BasePlayer player)
        {
            Emit(eventType, new Dictionary<string, object>
            {
                ["steamId"] = player.UserIDString,
                ["playerName"] = player.displayName
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
