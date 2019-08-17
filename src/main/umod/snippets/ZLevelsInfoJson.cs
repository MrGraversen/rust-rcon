using Newtonsoft.Json;

[ConsoleCommand("zl.json")]
void InfoCJsonCommand(ConsoleSystem.Arg arg)
{
    if (arg.Connection != null && arg.Connection.authLevel < 2) return;
    if (arg.Args == null || arg.Args.Length < 1)
    {
        return;
    }
    IPlayer player = this.covalence.Players.FindPlayer(arg.Args[0]);
    if (player == null)
    {
        return;
    }
    PlayerInfo playerData = null;
    if (!playerPrefs.PlayerInfo.TryGetValue(Convert.ToUInt64(player.Id), out playerData))
    {
        return;
    }

    var data = new Dictionary<string, string> {
        { "WCL", playerData.WCL.ToString() },
        { "ML", playerData.ML.ToString() },
        { "SL", playerData.SL.ToString() },
        { "AL", playerData.AL.ToString() },
        { "CL", playerData.CL.ToString() }
    };

    Puts(JsonConvert.SerializeObject(data));
}