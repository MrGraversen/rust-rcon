// #define DEBUG

using Newtonsoft.Json;
using Oxide.Core;
using Rust;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Text.RegularExpressions;
using UnityEngine;

namespace Oxide.Plugins
{
        using EnemyPrefabs = DeathNotes.RemoteConfiguration<Dictionary<string, string>>;
        using WeaponPrefabs = DeathNotes.RemoteConfiguration<Dictionary<string, string>>;
        using CombatEntityTypes = DeathNotes.RemoteConfiguration<Dictionary<string, DeathNotes.CombatEntityType>>;

        [Info("Death Notes", "LaserHydra/Mevent/Ownzone", "6.3.9")]
        [Description("Broadcasts deaths to chat along with detailed information")]
        class DeathNotes : RustPlugin
        {
                #region Fields

                private const string WildcardCharacter = "*";
                private const string CanSeePermission = "deathnotes.cansee";

                private static DeathNotes _instance;

                private PluginConfiguration _configuration;

                private readonly EnemyPrefabs _enemyPrefabs = new EnemyPrefabs("EnemyPrefabs");
                private readonly WeaponPrefabs _weaponPrefabs = new WeaponPrefabs("WeaponPrefabs");
                private readonly CombatEntityTypes _combatEntityTypes = new CombatEntityTypes("CombatEntityTypes");

                private readonly Regex _colorTagRegex =
                        new Regex(@"<color=.{0,7}>", RegexOptions.Compiled | RegexOptions.IgnoreCase);

                private readonly Regex _sizeTagRegex =
                        new Regex(@"<size=\d*>", RegexOptions.Compiled | RegexOptions.IgnoreCase);

                private readonly List<string> _richTextLiterals = new List<string>
                {
                        "</color>", "</size>", "<b>", "</b>", "<i>", "</i>"
                };

                private readonly Dictionary<ulong, AttackInfo> _previousAttack = new Dictionary<ulong, AttackInfo>();

                private readonly Func<PluginConfiguration.DeathMessage, DeathData, bool>[] _messageMatchingStages =
                {
                        (m, d) => MatchesCombatEntityType(d.KillerEntityType, m.KillerType) &&
                                  MatchesCombatEntityType(d.VictimEntityType, m.VictimType) &&
                                  MatchesDamageType(d.DamageType, m.DamageType),

                        (m, d) => MatchesCombatEntityType(d.KillerEntityType, m.KillerType) &&
                                  MatchesCombatEntityType(d.VictimEntityType, m.VictimType) &&
                                  m.DamageType == WildcardCharacter,

                        (m, d) => MatchesCombatEntityType(d.KillerEntityType, m.KillerType) &&
                                  m.VictimType == WildcardCharacter &&
                                  MatchesDamageType(d.DamageType, m.DamageType),

                        (m, d) => m.KillerType == WildcardCharacter &&
                                  MatchesCombatEntityType(d.VictimEntityType, m.VictimType) &&
                                  MatchesDamageType(d.DamageType, m.DamageType),

                        (m, d) => MatchesCombatEntityType(d.KillerEntityType, m.KillerType) &&
                                  m.VictimType == WildcardCharacter &&
                                  m.DamageType == WildcardCharacter,

                        (m, d) => m.KillerType == WildcardCharacter &&
                                  MatchesCombatEntityType(d.VictimEntityType, m.VictimType) &&
                                  m.DamageType == WildcardCharacter,

                        (m, d) => m.KillerType == WildcardCharacter &&
                                  m.VictimType == WildcardCharacter &&
                                  MatchesDamageType(d.DamageType, m.DamageType),

                        (m, d) => m.KillerType == WildcardCharacter &&
                                  m.VictimType == WildcardCharacter &&
                                  m.DamageType == WildcardCharacter
                };

                #endregion

                #region Hooks

                private void Init()
                {
                        _instance = this;

                        permission.RegisterPermission(CanSeePermission, this);

                        _configuration = Config.ReadObject<PluginConfiguration>();
                        _configuration.LoadDefaults();
                        Config.WriteObject(_configuration);

                        _enemyPrefabs.Load();
                        _weaponPrefabs.Load();
                        _combatEntityTypes.Load();
                }

                private void Unload()
                {
                        _instance = null;
                }

                private void OnEntityTakeDamage(BasePlayer victimEntity, HitInfo hitInfo)
                {
                        if (victimEntity == null || hitInfo == null) return;

                        // Don't track bleeding
                        if (victimEntity.lastDamage == DamageType.Bleeding)
                                return;

                        var userId = victimEntity.ToPlayer().userID;

                        _previousAttack[userId] = new AttackInfo
                        {
                                HitInfo = hitInfo,
                                Attacker = victimEntity.lastAttacker ?? hitInfo?.Initiator,
                                DamageType = victimEntity.lastDamage
                        };
                }

                private void OnEntityDeath(BaseCombatEntity victimEntity, HitInfo hitInfo)
                {
                        // Ignore - there is no victim for some reason
                        if (victimEntity == null)
                                return;

                        // Try to avoid error when entity was destroyed
                        if (victimEntity.gameObject == null)
                                return;

                        var data = new DeathData
                        {
                                VictimEntity = victimEntity,
                                KillerEntity = victimEntity.lastAttacker ?? hitInfo?.Initiator,
                                VictimEntityType = GetCombatEntityType(victimEntity),
                                KillerEntityType = GetCombatEntityType(victimEntity.lastAttacker),
                                DamageType = victimEntity.lastDamage,
                                HitInfo = hitInfo
                        };

                        // Handle inconsistencies/exceptions
                        HandleInconsistencies(ref data);

#if DEBUG
                        LogDebug("[DEATHNOTES DEBUG]");
                        LogDebug(
                                $"VictimEntity: {data.VictimEntity?.GetType().Name ?? "NULL"} / {data.VictimEntity?.ShortPrefabName ?? "NULL"} / {data.VictimEntity?.PrefabName ?? "NULL"}");
                        LogDebug(
                                $"KillerEntity: {data.KillerEntity?.GetType().Name ?? "NULL"} / {data.KillerEntity?.ShortPrefabName ?? "NULL"} / {data.KillerEntity?.PrefabName ?? "NULL"}");
                        LogDebug($"VictimEntityType: {data.VictimEntityType}");
                        LogDebug($"KillerEntityType: {data.KillerEntityType}");
                        LogDebug($"DamageType: {data.DamageType}");
                        LogDebug($"Bodypart: {GetCustomizedBodypartName(data.HitInfo)}");
                        LogDebug($"Weapon: {hitInfo?.WeaponPrefab?.ShortPrefabName ?? "NULL"}");
#endif

                        // Change entity type for dwellers
                        RepairEntityTypes(ref data);

                        // Ignore deaths of other entities
                        if (data.KillerEntityType == CombatEntityType.Other || data.VictimEntityType == CombatEntityType.Other)
                                return;

                        // Ignore deaths which don't involve players or the helicopter which usually does not track a player as killer
                        if (data.VictimEntityType != CombatEntityType.Player && data.KillerEntityType != CombatEntityType.Player &&
                            data.VictimEntityType != CombatEntityType.Helicopter)
                                return;

                        // Populate the variables in the message
                        string message = PopulateMessageVariables(
                                // Find the best matching death message for this death
                                GetDeathMessage(data),
                                data
                        );

                        if (message == null)
                                return;

                        object hookResult = Interface.Call("OnDeathNotice", data.ToDictionary(), message);

                        if (hookResult?.Equals(false) ?? false)
                                return;

                        if (_configuration.ShowInChat)
                        {
                                foreach (var player in BasePlayer.activePlayerList)
                                {
                                        if (_configuration.RequirePermission &&
                                            !permission.UserHasPermission(player.UserIDString, CanSeePermission))
                                                continue;

                                        if (_configuration.MessageRadius != -1 &&
                                            player.Distance(data.VictimEntity) > _configuration.MessageRadius)
                                                continue;

                                        Player.Reply(
                                                player,
                                                _configuration.ChatFormat.Replace("{message}", message),
                                                ulong.Parse(_configuration.ChatIcon)
                                        );
                                }
                        }

                        if (_configuration.ShowInConsole)
                                Puts(StripRichText(message));
                }

                private void RepairEntityTypes(ref DeathData data)
                {
                        if (data.VictimEntity != null)
                        {
                                string victimPrefabName = data.VictimEntity.ShortPrefabName.ToLower();
                                if (victimPrefabName.Contains("underwaterdweller"))
                                {
                                        data.VictimEntityType = CombatEntityType.UnderwaterDweller;
                                }

                                if (victimPrefabName.Contains("tunneldweller"))
                                {
                                        data.VictimEntityType = CombatEntityType.TunnelDweller;
                                }
                        }

                        if (data.KillerEntity != null)
                        {
                                string killerPrefabName = data.KillerEntity.ShortPrefabName.ToLower();

                                if (killerPrefabName.Contains("underwaterdweller"))
                                {
                                        data.KillerEntityType = CombatEntityType.UnderwaterDweller;
                                }

                                if (killerPrefabName.Contains("tunneldweller"))
                                {
                                        data.KillerEntityType = CombatEntityType.TunnelDweller;
                                }
                        }
                }

                private void OnFlameThrowerBurn(FlameThrower flameThrower, BaseEntity baseEntity)
                {
                        if (flameThrower == null || baseEntity == null) return;

                        var flame = baseEntity.gameObject.AddComponent<Flame>();
                        flame.Source = Flame.FlameSource.Flamethrower;
                        flame.SourceEntity = flameThrower;
                        flame.Initiator = flameThrower.GetOwnerPlayer();
                }

                private void OnFlameExplosion(FlameExplosive explosive, BaseEntity baseEntity)
                {
                        if (explosive == null || baseEntity == null) return;

                        var flame = baseEntity.gameObject.AddComponent<Flame>();
                        flame.Source = Flame.FlameSource.IncendiaryProjectile;
                        flame.SourceEntity = explosive;
                        flame.Initiator = explosive.creatorEntity;
                }

                private void OnFireBallSpread(FireBall fireBall, BaseEntity newFire)
                {
                        if (fireBall == null) return;

                        var flame = fireBall.GetComponent<Flame>();
                        if (flame == null) return;

                        var newFlame = newFire.gameObject.AddComponent<Flame>();
                        newFlame.Source = flame.Source;
                        newFlame.SourceEntity = flame.SourceEntity;
                        newFlame.Initiator = flame.Initiator;
                }

                private void OnFireBallDamage(FireBall fireBall, BaseCombatEntity target, HitInfo hitInfo) =>
                        hitInfo.Initiator = fireBall;

                #endregion

                #region Death Messages

                private string GetDeathMessage(DeathData data)
                {
                        foreach (var matchingStage in _messageMatchingStages)
                        {
                                var match = _configuration.Translations.Messages.Find(m => matchingStage.Invoke(m, data));

                                if (match != null)
                                        return match.Messages.GetRandom((uint) DateTime.UtcNow.Millisecond);
                        }

                        return null;
                }

                private string PopulateMessageVariables(string message, DeathData data)
                {
                        if (string.IsNullOrEmpty(message))
                                return null;

                        var replacements = new Dictionary<string, string>
                        {
                                ["victim"] = GetCustomizedEntityName(data.VictimEntity, data.VictimEntityType)
                        };

                        if (data.KillerEntityType != CombatEntityType.None)
                        {
                                replacements.Add("killer", GetCustomizedEntityName(data.KillerEntity, data.KillerEntityType));
                                replacements.Add("bodypart", GetCustomizedBodypartName(data.HitInfo));

                                if (data.KillerEntity != null)
                                {
                                        var distance = data.KillerEntity.Distance(data.VictimEntity);
                                        replacements.Add("distance", GetDistance(distance, _configuration.UseMetricDistance));
                                }

                                if (data.KillerEntityType == CombatEntityType.Player)
                                {
                                        replacements.Add("hp", data.KillerEntity.Health().ToString("#0.#"));
                                        replacements.Add("weapon", GetCustomizedWeaponName(data));
                                        replacements.Add("attachments", string.Join(", ", GetCustomizedAttachmentNames(data.HitInfo)));
                                }
                                else if (data.KillerEntityType == CombatEntityType.Turret
                                         || data.KillerEntityType == CombatEntityType.Lock
                                         || data.KillerEntityType == CombatEntityType.Trap)
                                {
                                        replacements.Add("owner",
                                                covalence.Players.FindPlayerById(data.KillerEntity.OwnerID.ToString())?.Name ?? "unknown owner"
                                        ); // TODO: Work on the potential unknown owner case
                                }
                        }

                        message = InsertPlaceholderValues(message, replacements);
                        EmitRawDeathNotice(data, replacements, message);

                        replacements = null;
                        return message;
                }

                private struct DeathData
                {
                        public CombatEntityType VictimEntityType { get; set; }
                        [JsonIgnore] public BaseCombatEntity VictimEntity { get; set; }

                        public CombatEntityType KillerEntityType { get; set; }
                        [JsonIgnore] public BaseEntity KillerEntity { get; set; }

                        public DamageType DamageType { get; set; }
                        [JsonIgnore] public HitInfo HitInfo { get; set; }

                        public Dictionary<string, object> ToDictionary() => new Dictionary<string, object>
                        {
                                ["VictimEntityType"] = VictimEntityType,
                                ["VictimEntity"] = VictimEntity,
                                ["KillerEntityType"] = KillerEntityType,
                                ["KillerEntity"] = KillerEntity,
                                ["DamageType"] = DamageType,
                                ["HitInfo"] = HitInfo
                        };
                }

                #endregion

                #region Entity Identification

                private CombatEntityType GetCombatEntityType(BaseEntity entity)
                {
                        if (entity == null)
                                return CombatEntityType.None;

                        if (_combatEntityTypes.Contents != null)
                        {
                                if (_combatEntityTypes.Contents.ContainsKey(entity.GetType().Name))
                                        return _combatEntityTypes.Contents[entity.GetType().Name];

                                if (_combatEntityTypes.Contents.ContainsKey(entity.ShortPrefabName))
                                        return _combatEntityTypes.Contents[entity.ShortPrefabName];
                        }

                        if (entity is BaseOven)
                                return CombatEntityType.HeatSource;

                        if (entity is SimpleBuildingBlock)
                                return CombatEntityType.ExternalWall;

                        if (entity is BaseAnimalNPC)
                                return CombatEntityType.Animal;

                        if (entity is BaseTrap)
                                return CombatEntityType.Trap;

                        if (entity is Barricade)
                                return CombatEntityType.Barricade;

                        if (entity is IOEntity)
                                return CombatEntityType.Trap;

                        if (entity is ScientistNPC)
                                return CombatEntityType.Scientist;

                        if (entity.GetType().Name.Equals("ZombieNPC"))
                                return CombatEntityType.ZombieNPC;

                        return CombatEntityType.Other;
                }

                private string GetCustomizedEntityName(BaseEntity entity, CombatEntityType combatEntityType)
                {
                        var name = GetEntityName(entity, combatEntityType);

                        if (string.IsNullOrEmpty(name))
                                return null;

                        // Don't load player names into config
                        if (combatEntityType == CombatEntityType.Player)
                                return name;

                        if (!_configuration.Translations.Names.ContainsKey(name))
                        {
                                _configuration.Translations.Names.Add(name, name);
                                Config.WriteObject(_configuration);
                        }

                        return _configuration.Translations.Names[name];
                }

                private string GetEntityName(BaseEntity entity, CombatEntityType combatEntityType)
                {
                        // Entity may be null for helicopter or bradley, see HandleExceptions(...)
                        if (entity == null &&
                            combatEntityType != CombatEntityType.Helicopter &&
                            combatEntityType != CombatEntityType.Bradley)
                                return null;

                        switch (combatEntityType)
                        {
                                case CombatEntityType.Player:
                                        return StripRichText(entity.ToPlayer().displayName);

                                case CombatEntityType.Murderer:
                                case CombatEntityType.Scarecrow:
                                case CombatEntityType.Scientist:
                                case CombatEntityType.ZombieNPC:
                                        var name = entity.ToPlayer()?.displayName;

                                        if (!string.IsNullOrEmpty(name) && name != entity.ToPlayer()?.userID.ToString())
                                        {
                                                return name;
                                        }

                                        if (!_enemyPrefabs.Contents.ContainsKey(entity.ShortPrefabName))
                                        {
                                                return combatEntityType.ToString();
                                        }

                                        break;

                                case CombatEntityType.TunnelDweller:
                                        return "Tunnel Dweller";

                                case CombatEntityType.UnderwaterDweller:
                                        return "Underwater Dweller";

                                case CombatEntityType.Helicopter:
                                        return "Helicopter";

                                case CombatEntityType.Bradley:
                                        return "Bradley APC";

                                case CombatEntityType.Sentry:
                                        return "Sentry";

                                case CombatEntityType.Fire:
                                        return entity.creatorEntity?.ToPlayer()?.displayName ?? "Fire";
                        }

                        if (_enemyPrefabs.Contents.ContainsKey(entity.ShortPrefabName))
                                return _enemyPrefabs.Contents[entity.ShortPrefabName];

                        return HumanizePascalCase(entity.GetType().Name);
                }

                internal enum CombatEntityType
                {
                        Helicopter = 0,
                        Bradley = 1,
                        Animal = 2,
                        Murderer = 3,
                        Scientist = 4,
                        Player = 5,
                        Trap = 6,
                        Turret = 7,
                        Barricade = 8,
                        ExternalWall = 9,
                        HeatSource = 10,
                        Fire = 11,
                        Lock = 12,
                        Sentry = 13,
                        Other = 14,
                        None = 15,
                        Scarecrow = 16,
                        TunnelDweller = 17,
                        UnderwaterDweller = 18,
                        ZombieNPC = 19
                }

                #endregion

                #region Workarounds and Inconsistency Handling

                private void HandleInconsistencies(ref DeathData data)
                {
                        // Deaths of other entity types are not of interest and might cause errors
                        if (data.VictimEntityType == CombatEntityType.Other)
                                return;

                        if (data.KillerEntity is FireBall)
                                data.DamageType = DamageType.Heat;

                        // If the killer entity is null, but a weapon is given, we might be able to fall back to the parent entity of that weapon
                        // Notably for the auto turret after the changes it has had
                        if (data.KillerEntity == null && data.HitInfo?.Weapon != null)
                        {
                                data.KillerEntity = data.HitInfo.Weapon.GetParentEntity();
                                data.KillerEntityType = GetCombatEntityType(data.KillerEntity);
                        }

                        // Get previous attacker when bleeding out
                        if (data.VictimEntityType == CombatEntityType.Player &&
                            (data.DamageType == DamageType.Bleeding || data.HitInfo == null))
                        {
                                var userId = data.VictimEntity.ToPlayer().userID;

                                if (_previousAttack.ContainsKey(userId))
                                {
                                        var attack = _previousAttack[userId];
                                        data.KillerEntity = attack.Attacker;
                                        data.KillerEntityType = GetCombatEntityType(data.KillerEntity);

                                        // Restore previous hitInfo for weapon determination
                                        if (attack.HitInfo != null)
                                                data.HitInfo = attack.HitInfo;

                                        // Use previous damagetype if this is a selfinflicted death,
                                        // so falling to death etc. is also shown when wounded and bleeding out
                                        if (data.KillerEntity == null || data.KillerEntity == data.VictimEntity)
                                                data.DamageType = attack.DamageType;
                                        else
                                                data.DamageType = DamageType.Bleeding;
                                }
                        }

                        if (data.KillerEntityType != CombatEntityType.None && data.KillerEntity != null)
                        {
                                // Workaround for deaths caused by flamethrower or rocket fire
                                var flame = data.KillerEntity.gameObject.GetComponent<Flame>();
                                if (flame != null && flame.Initiator != null)
                                {
                                        data.KillerEntity = flame.Initiator;
                                        data.KillerEntityType = CombatEntityType.Player;
                                        return;
                                }
                        }

                        // Bradley kill with main cannon
                        if (data.HitInfo?.WeaponPrefab?.ShortPrefabName == "maincannonshell")
                        {
                                data.KillerEntityType = CombatEntityType.Bradley;
                                return;
                        }

                        if (data.HitInfo?.WeaponPrefab?.ShortPrefabName?.StartsWith("rocket_heli") ?? false)
                        {
                                data.KillerEntityType = CombatEntityType.Helicopter;
                                return;
                        }

                        // Vehicle Kills
                        if (data.KillerEntityType == CombatEntityType.Player
                            && data.DamageType == DamageType.Generic
                            && data.KillerEntity.ToPlayer().isMounted)
                        {
                                data.DamageType = DamageType.Collision;
                                return;
                        }
                }

                private struct AttackInfo
                {
                        public HitInfo HitInfo { get; set; }
                        public DamageType DamageType { get; set; }
                        public BaseEntity Attacker { get; set; }
                }

                private class Flame : MonoBehaviour
                {
                        public FlameSource Source { get; set; }
                        public BaseEntity SourceEntity { get; set; }
                        public BaseEntity Initiator { get; set; }

                        public enum FlameSource
                        {
                                Flamethrower,
                                IncendiaryProjectile
                        }
                }

                #endregion

                #region Weapons

                private string GetCustomizedWeaponName(DeathData deathData)
                {
                        var name = GetWeaponName(deathData);

                        if (string.IsNullOrEmpty(name))
                                return null;

                        if (!_configuration.Translations.Weapons.ContainsKey(name))
                        {
                                _configuration.Translations.Weapons.Add(name, name);
                                Config.WriteObject(_configuration);
                        }

                        return _configuration.Translations.Weapons[name];
                }

                private string GetWeaponName(DeathData deathData)
                {
                        if (deathData.HitInfo == null)
                                return null;

                        Item item = deathData.HitInfo.Weapon?.GetItem();
                        /*var parentEntity = hitInfo.Weapon?.GetParentEntity();
                        Item item = null;

                        if (parentEntity is BasePlayer)
                        {
                            (parentEntity as BasePlayer).inventory.FindItemUID(hitInfo.Weapon.ownerItemUID);
                        }
                        else if (parentEntity is ContainerIOEntity)
                        {
                            (parentEntity as ContainerIOEntity).inventory.FindItemByUID(hitInfo.Weapon.ownerItemUID);
                        }*/

                        if (item != null)
                                return item.info.displayName.english;

                        var prefab = deathData.HitInfo.Initiator?.GetComponent<Flame>()?.SourceEntity?.ShortPrefabName ??
                                     deathData.HitInfo.WeaponPrefab?.ShortPrefabName;

                        if (prefab != null)
                        {
                                if (_weaponPrefabs.Contents.ContainsKey(prefab))
                                        return _weaponPrefabs.Contents[prefab];

                                return prefab;
                        }

                        // Vehicles are the only thing we classify as a weapon, while not being classified as such by the game.
                        // TODO: Having this here kinda sucks, make this better.
                        if (deathData.DamageType == DamageType.Collision)
                        {
                                return "Vehicle";
                        }

                        return null;
                }

                private string[] GetCustomizedAttachmentNames(HitInfo info)
                {
                        var items = info?.Weapon?.GetItem()?.contents?.itemList;

                        if (items == null)
                        {
                                return Array.Empty<string>();
                        }

                        return items.Select(i => GetCustomizedAttachmentName(i.info.displayName.english)).ToArray();
                }

                private string GetCustomizedAttachmentName(string name)
                {
                        if (!_configuration.Translations.Attachments.ContainsKey(name))
                        {
                                _configuration.Translations.Attachments.Add(name, name);
                                Config.WriteObject(_configuration);
                        }

                        return _configuration.Translations.Attachments[name];
                }

                #endregion

                #region Bodyparts

                private string GetCustomizedBodypartName(HitInfo hitInfo)
                {
                        var name = GetBodypartName(hitInfo);

                        if (string.IsNullOrEmpty(name))
                                return null;

                        if (!_configuration.Translations.Bodyparts.ContainsKey(name))
                        {
                                _configuration.Translations.Bodyparts.Add(name, name);
                                Config.WriteObject(_configuration);
                        }

                        return _configuration.Translations.Bodyparts[name];
                }

                private string GetBodypartName(HitInfo hitInfo)
                {
                        var hitArea = hitInfo?.boneArea ?? (HitArea) (-1);
                        return (int) hitArea == -1 ? "Body" : hitArea.ToString();
                }

                #endregion

                #region Helper

#if DEBUG
                private static void LogDebug(string text)
                {
                        if (BasePlayer.activePlayerList.Count >= 1)
                        {
                                BasePlayer.activePlayerList[0].ConsoleMessage($"<color=orange>{text}</color>");
                        }
                }
#endif

                private static string GetDistance(float meters, bool useMetric)
                {
                        double value = Math.Round(useMetric ? meters : meters * 3.28f, 1);
                        string unit = value == 1
                                ? _instance.lang.GetMessage("Distance Unit Singular", _instance)
                                : _instance.lang.GetMessage("Distance Unit Plural", _instance);

                        return $"{value} {unit}";
                }

                private static string ApplyVariableFormat(string text, string variableName)
                {
                        if (_instance._configuration.VariableFormats.ContainsKey(variableName))
                        {
                                var format = _instance._configuration.VariableFormats[variableName];
                                text = format.Replace("{value}", text);
                        }

                        return text;
                }

                private static string InsertPlaceholderValues(string text, Dictionary<string, string> values)
                {
                        foreach (var kvp in values)
                        {
                                string value = ApplyVariableFormat(kvp.Value, kvp.Key);
                                if (string.IsNullOrEmpty(kvp.Value))
                                {
                                        text = text.Replace($"{{{kvp.Key}}}", string.Empty);
                                }
                                else if (_instance._configuration.VariableColors.ContainsKey(kvp.Key))
                                {
                                        var color = _instance._configuration.VariableColors[kvp.Key];
                                        text = text.Replace($"{{{kvp.Key}}}", $"<color={color}>{value}</color>");
                                        color = null;
                                }
                                else
                                {
                                        text = text.Replace($"{{{kvp.Key}}}", value);
                                }
                        }

                        return text;
                }

                private static string HumanizePascalCase(string text)
                {
                        if (string.IsNullOrEmpty(text))
                                return string.Empty;

                        var sb = new StringBuilder();

                        foreach (char c in text)
                        {
                                if (char.IsUpper(c) && sb.Length != 0 && !char.IsUpper(sb[sb.Length - 1]))
                                        sb.Append(" ");

                                sb.Append(c);
                        }

                        return sb.ToString();
                }

                private string StripRichText(string text)
                {
                        if (string.IsNullOrEmpty(text))
                                return string.Empty;

                        text = _colorTagRegex.Replace(text, string.Empty);
                        text = _sizeTagRegex.Replace(text, string.Empty);

                        foreach (var richTextLiteral in _richTextLiterals)
                                text = text.Replace(richTextLiteral, string.Empty);

                        return text;
                }

                private static bool MatchesCombatEntityType(CombatEntityType combatEntityType, string text)
                {
                        if (combatEntityType == CombatEntityType.None && text == "-")
                                return true;

                        return combatEntityType.ToString().Equals(text);
                }

                private static bool MatchesDamageType(DamageType damageType, string text)
                {
                        return damageType.ToString().Equals(text);
                }

                #endregion

                #region Configuration

                protected override void LoadDefaultMessages()
                {
                        lang.RegisterMessages(new Dictionary<string, string>
                        {
                                ["Distance Unit Singular"] = "meter",
                                ["Distance Unit Plural"] = "meters"
                        }, this);
                }

                protected override void LoadDefaultConfig() => PrintWarning("Generating new configuration file...");

                private sealed class PluginConfiguration
                {
                        [JsonProperty("Translations")] public Translation Translations = new Translation();

                        [JsonProperty("Variable Formats")] public Dictionary<string, string> VariableFormats =
                                new Dictionary<string, string>
                                {
                                        ["attachments"] = " ({value})"
                                };

                        [JsonProperty("Variable Colors")] public Dictionary<string, string> VariableColors =
                                new Dictionary<string, string>
                                {
                                        ["killer"] = "#C4FF00",
                                        ["victim"] = "#C4FF00",
                                        ["weapon"] = "#C4FF00",
                                        ["attachments"] = "#C4FF00",
                                        ["distance"] = "#C4FF00",
                                        ["owner"] = "#C4FF00"
                                };

                        [JsonProperty("Chat Format")]
                        public string ChatFormat = "<color=#838383>[<color=#80D000>DeathNotes</color>] {message}</color>";

                        [JsonProperty("Chat Icon (SteamID)")] public string ChatIcon = "76561198077847390";

                        [JsonProperty("Show Kills in Console")]
                        public bool ShowInConsole = true;

                        [JsonProperty("Show Kills in Chat")] public bool ShowInChat = true;

                        [JsonProperty("Message Broadcast Radius (in meters)")]
                        public int MessageRadius = -1;

                        [JsonProperty("Use Metric Distance")] public bool UseMetricDistance = true;

                        [JsonProperty("Require Permission (deathnotes.cansee)")]
                        public bool RequirePermission = false;

                        public void LoadDefaults()
                        {
                                if (Translations.Messages == null)
                                {
                                        var defaults = new RemoteConfiguration<List<DeathMessage>>("DefaultMessages");
                                        defaults.Load(success =>
                                        {
                                                if (success)
                                                {
                                                        Translations.Messages = defaults.Contents;
                                                        _instance.Config.WriteObject(this);
                                                }
                                        });
                                }
                        }

                        public class DeathMessage
                        {
                                public string KillerType { get; set; }
                                public string VictimType { get; set; }
                                public string DamageType { get; set; }

                                public string[] Messages { get; set; }

                                protected bool Equals(DeathMessage other) => string.Equals(KillerType, other.KillerType) &&
                                                                             string.Equals(VictimType, other.VictimType) &&
                                                                             string.Equals(DamageType, other.DamageType);
                        }

                        public class Translation
                        {
                                [JsonProperty("Death Messages")] public List<DeathMessage> Messages;

                                [JsonProperty("Names")] public Dictionary<string, string> Names = new Dictionary<string, string>();

                                [JsonProperty("Bodyparts")]
                                public Dictionary<string, string> Bodyparts = new Dictionary<string, string>();

                                [JsonProperty("Weapons")] public Dictionary<string, string> Weapons = new Dictionary<string, string>();

                                [JsonProperty("Attachments")]
                                public Dictionary<string, string> Attachments = new Dictionary<string, string>();
                        }
                }

                internal sealed class RemoteConfiguration<T>
                {
                        private const string Host = "http://files.laserhydra.com/config/DeathNotes/v6.3.6/";

                        private readonly string _file;

                        public RemoteConfiguration(string file)
                        {
                                _file = file;
                        }

                        public T Contents { get; private set; }
                        private string ExactUrl => Host + _file;

                        public void Load(Action<bool> callback = null)
                        {
                                _instance.webrequest.Enqueue(ExactUrl, string.Empty, (code, response) =>
                                {
                                        try
                                        {
                                                if (!IsSuccessStatusCode(code))
                                                        throw new Exception($"Status code indicates failure. Code: {code}");

                                                Contents = JsonConvert.DeserializeObject<T>(response);
                                                callback?.Invoke(true);

                                                Interface.Oxide.DataFileSystem.WriteObject($"{nameof(DeathNotes)}/{_file}", Contents);
                                        }
                                        catch (Exception ex)
                                        {
                                                if (Interface.Oxide.DataFileSystem.ExistsDatafile($"{nameof(DeathNotes)}/{_file}"))
                                                {
                                                        Contents = Interface.Oxide.DataFileSystem.ReadObject<T>($"{nameof(DeathNotes)}/{_file}");

                                                        _instance.PrintWarning(
                                                                $"Could not load remote config '{_file}'. The plugin will be using the previously downloaded file.");

                                                        callback?.Invoke(true);
                                                }
                                                else
                                                {
                                                        _instance.PrintError(
                                                                $"Could not load remote config '{_file}'. The plugin will not work properly. Please check whether you can access {ExactUrl} via your browser. If you can, please check the FAQ on how to solve this.");
                                                        _instance.PrintError($"[Code {code}] {ex.GetType().Name}: {ex.Message}");
                                                        _instance.PrintError($"Response: {response}");

                                                        callback?.Invoke(false);
                                                }
                                        }
                                }, _instance);
                        }

                        private bool IsSuccessStatusCode(int code) => code >= 200 && code < 300;
                }

                #endregion

                private void EmitRawDeathNotice(DeathData data, Dictionary<string, string> values, string message)
        {
            // Post-process the values
            values.Add("killerId", data.KillerEntity.ToPlayer()?.userID.ToString());
            values.Add("victimId", data.VictimEntity.ToPlayer()?.userID.ToString());
            values.Add("damageType", data.DamageType.ToString());
            values.Add("killerEntityType", data.KillerEntityType.ToString());
            values.Add("victimEntityType", data.VictimEntityType.ToString());

            // Emit data hook
            Interface.Call("OnRawDeathNotice", values, message);
        }
        }
}
