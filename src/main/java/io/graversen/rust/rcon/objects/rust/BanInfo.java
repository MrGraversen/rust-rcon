package io.graversen.rust.rcon.objects.rust;

import com.google.gson.annotations.SerializedName;

public class BanInfo
{
    @SerializedName("steamid")
    private final String steamId;
    private final String username;
    private final String notes;

    public BanInfo(String steamId, String username, String notes)
    {
        this.steamId = steamId;
        this.username = username;
        this.notes = notes;
    }

    public String getSteamId()
    {
        return steamId;
    }

    public String getUsername()
    {
        return username;
    }

    public String getNotes()
    {
        return notes;
    }
}
