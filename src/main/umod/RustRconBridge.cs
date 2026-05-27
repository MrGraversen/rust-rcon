using System;
using System.Collections.Generic;
using System.Globalization;
using Newtonsoft.Json;
using Oxide.Core.Libraries.Covalence;
using UnityEngine;

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

        private void OnPlayerReported(BasePlayer reporter, string targetName, string targetId, string subject, string message, string type)
        {
            Emit("player.reported", new Dictionary<string, object>
            {
                ["reporterSteamId"] = reporter?.UserIDString ?? string.Empty,
                ["reporterName"] = reporter?.displayName ?? string.Empty,
                ["targetSteamId"] = targetId ?? string.Empty,
                ["targetName"] = targetName ?? string.Empty,
                ["subject"] = subject ?? string.Empty,
                ["message"] = message ?? string.Empty,
                ["reportType"] = type ?? string.Empty
            });
        }

        private object OnPlayerViolation(BasePlayer player, AntiHackType type, float amount, GameObject gameObject)
        {
            Emit("player.violation", new Dictionary<string, object>
            {
                ["steamId"] = player?.UserIDString ?? string.Empty,
                ["playerName"] = player?.displayName ?? string.Empty,
                ["violationType"] = type.ToString(),
                ["amount"] = amount
            });

            return null;
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

        private void OnTeamCreated(BasePlayer player, RelationshipManager.PlayerTeam team)
        {
            EmitTeam("created", team, player, 0);
        }

        private void OnTeamDisbanded(RelationshipManager.PlayerTeam team)
        {
            EmitTeam("disbanded", team, null, 0);
        }

        private object OnTeamAcceptInvite(RelationshipManager.PlayerTeam team, BasePlayer player)
        {
            EmitTeam("joined", team, player, 0);
            return null;
        }

        private object OnTeamLeave(RelationshipManager.PlayerTeam team, BasePlayer player)
        {
            EmitTeam("left", team, player, 0);
            return null;
        }

        private object OnTeamKick(RelationshipManager.PlayerTeam team, BasePlayer player, ulong target)
        {
            EmitTeam("kicked", team, player, target);
            return null;
        }

        private void OnExplosiveThrown(BasePlayer player, BaseEntity entity, ThrownWeapon item)
        {
            EmitExplosiveUse("thrown", player, item?.GetType().Name ?? string.Empty, entity);
        }

        private void OnRocketLaunched(BasePlayer player, BaseEntity entity)
        {
            EmitExplosiveUse("rocket", player, string.Empty, entity);
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

        private void EmitTeam(string teamEventType, RelationshipManager.PlayerTeam team, BasePlayer actor, ulong target)
        {
            Emit("team", new Dictionary<string, object>
            {
                ["teamId"] = team?.teamID ?? 0,
                ["leaderSteamId"] = team?.teamLeader ?? 0,
                ["teamEventType"] = teamEventType,
                ["actorSteamId"] = actor?.UserIDString ?? string.Empty,
                ["actorName"] = actor?.displayName ?? string.Empty,
                ["targetSteamId"] = target == 0 ? string.Empty : target.ToString(),
                ["members"] = TeamMembers(team)
            });
        }

        private void EmitExplosiveUse(string explosiveUseType, BasePlayer player, string weapon, BaseEntity entity)
        {
            Emit("explosive.use", new Dictionary<string, object>
            {
                ["steamId"] = player?.UserIDString ?? string.Empty,
                ["playerName"] = player?.displayName ?? string.Empty,
                ["explosiveUseType"] = explosiveUseType,
                ["weapon"] = weapon ?? string.Empty,
                ["entity"] = entity?.ShortPrefabName ?? string.Empty,
                ["position"] = Position(entity)
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

        private List<string> TeamMembers(RelationshipManager.PlayerTeam team)
        {
            var members = new List<string>();
            if (team?.members == null)
            {
                return members;
            }

            foreach (var member in team.members)
            {
                members.Add(member.ToString());
            }

            return members;
        }

        private string Position(BaseEntity entity)
        {
            return entity == null ? string.Empty : Position(entity.transform.position);
        }

        private string Position(Vector3 position)
        {
            return string.Format(
                CultureInfo.InvariantCulture,
                "{0:0.##},{1:0.##},{2:0.##}",
                position.x,
                position.y,
                position.z
            );
        }
    }
}
