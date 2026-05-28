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
        private readonly Dictionary<string, Dictionary<string, object>> crateHackers = new Dictionary<string, Dictionary<string, object>>();

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

        private object CanHackCrate(BasePlayer player, HackableLockedCrate crate)
        {
            crateHackers[EntityId(crate)] = new Dictionary<string, object>
            {
                ["steamId"] = player?.UserIDString ?? string.Empty,
                ["playerName"] = player?.displayName ?? string.Empty
            };

            return null;
        }

        private void OnCrateLanded(HackableLockedCrate crate)
        {
            EmitWorldEvent("locked_crate_landed", CrateAttributes(crate));
        }

        private void OnCrateHack(HackableLockedCrate crate)
        {
            var attributes = CrateAttributes(crate);
            var entityId = EntityId(crate);
            if (crateHackers.TryGetValue(entityId, out var hacker))
            {
                foreach (var attribute in hacker)
                {
                    attributes[attribute.Key] = attribute.Value;
                }
            }

            EmitWorldEvent("locked_crate_hack_started", attributes);
        }

        private void OnCrateHackEnd(HackableLockedCrate crate)
        {
            var attributes = CrateAttributes(crate);
            var entityId = EntityId(crate);
            if (crateHackers.TryGetValue(entityId, out var hacker))
            {
                foreach (var attribute in hacker)
                {
                    attributes[attribute.Key] = attribute.Value;
                }

                crateHackers.Remove(entityId);
            }

            EmitWorldEvent("locked_crate_hack_completed", attributes);
        }

        private object OnCargoShipHarborApproach(CargoShip ship, CargoNotifier notifier)
        {
            EmitWorldEvent("cargo_ship_harbor_approach", EntityAttributes(ship));
            return null;
        }

        private void OnCargoShipHarborArrived(CargoShip ship)
        {
            EmitWorldEvent("cargo_ship_harbor_arrived", EntityAttributes(ship));
        }

        private void OnCargoShipHarborLeave(CargoShip ship)
        {
            EmitWorldEvent("cargo_ship_harbor_left", EntityAttributes(ship));
        }

        private void OnAirdrop(CargoPlane plane, Vector3 newDropPosition)
        {
            var attributes = EntityAttributes(plane);
            attributes["dropPosition"] = Position(newDropPosition);
            EmitWorldEvent("airdrop", attributes);
        }

        private void OnSupplyDropDropped(BaseEntity supplyDrop, CargoPlane plane)
        {
            var attributes = EntityAttributes(supplyDrop);
            attributes["planeEntityId"] = EntityId(plane);
            attributes["planePosition"] = Position(plane);
            EmitWorldEvent("supply_drop_dropped", attributes);
        }

        private void OnSupplyDropLanded(SupplyDrop supplyDrop)
        {
            EmitWorldEvent("supply_drop_landed", EntityAttributes(supplyDrop));
        }

        private object OnPatrolHelicopterKill(PatrolHelicopter helicopter, HitInfo info)
        {
            var attributes = EntityAttributes(helicopter);
            AddPlayerAttributes(attributes, "killer", info?.InitiatorPlayer);
            EmitWorldEvent("patrol_helicopter_killed", attributes);
            return null;
        }

        private void OnEntityDeath(BaseCombatEntity entity, HitInfo info)
        {
            if (!(entity is BradleyAPC))
            {
                return;
            }

            var attributes = EntityAttributes(entity);
            AddPlayerAttributes(attributes, "killer", info?.InitiatorPlayer);
            EmitWorldEvent("bradley_apc_destroyed", attributes);
        }

        private void OnMlrsFired(MLRS mlrs, BasePlayer owner)
        {
            var attributes = EntityAttributes(mlrs);
            AddPlayerAttributes(attributes, "owner", owner);
            EmitWorldEvent("mlrs_fired", attributes);
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

        private void EmitWorldEvent(string worldEvent, Dictionary<string, object> attributes)
        {
            Emit("world.event", new Dictionary<string, object>
            {
                ["worldEvent"] = worldEvent,
                ["attributes"] = attributes
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

        private Dictionary<string, object> CrateAttributes(HackableLockedCrate crate)
        {
            return EntityAttributes(crate);
        }

        private Dictionary<string, object> EntityAttributes(BaseEntity entity)
        {
            return new Dictionary<string, object>
            {
                ["entityId"] = EntityId(entity),
                ["entity"] = entity?.ShortPrefabName ?? string.Empty,
                ["position"] = Position(entity)
            };
        }

        private void AddPlayerAttributes(Dictionary<string, object> attributes, string prefix, BasePlayer player)
        {
            attributes[$"{prefix}SteamId"] = player?.UserIDString ?? string.Empty;
            attributes[$"{prefix}Name"] = player?.displayName ?? string.Empty;
        }

        private string EntityId(BaseEntity entity)
        {
            return entity?.net?.ID.ToString() ?? string.Empty;
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
