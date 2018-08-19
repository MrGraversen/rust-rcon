package io.graversen.rust.rcon.objects.rust;

import com.google.gson.annotations.SerializedName;

public class Player
{
    @SerializedName("SteamID")
    private String steamId;

    @SerializedName("OwnerSteamID")
    private String ownerSteamId;

    @SerializedName("DisplayName")
    private String displayName;

    @SerializedName("Ping")
    private int ping;

    @SerializedName("Address")
    private String ipAddress;

    @SerializedName("ConnectedSeconds")
    private int connectedSeconds;

    @SerializedName("Health")
    private double health;

    public String getSteamId()
    {
        return steamId;
    }

    public String getOwnerSteamId()
    {
        return ownerSteamId;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public int getPing()
    {
        return ping;
    }

    public String getIpAddress()
    {
        return ipAddress;
    }

    public int getConnectedSeconds()
    {
        return connectedSeconds;
    }

    public double getHealth()
    {
        return health;
    }
}
