package io.graversen.v1.rust.rcon.objects.rust;

import com.google.gson.annotations.SerializedName;

public class ServerInfo
{
    @SerializedName("Hostname")
    private String hostName;

    @SerializedName("MaxPlayers")
    private int maxPlayers;

    @SerializedName("Players")
    private int currentPlayers;

    @SerializedName("Queued")
    private int queuedPlayers;

    @SerializedName("Joining")
    private int joiningPlayers;

    @SerializedName("EntityCount")
    private int entityCount;

    @SerializedName("Uptime")
    private int uptimeSeconds;

    @SerializedName("Map")
    private String map;

    @SerializedName("Framerate")
    private double frameRate;

    @SerializedName("Memory")
    private int memoryUsageMb;

    @SerializedName("Collections")
    private int collections;

    @SerializedName("NetworkIn")
    private int networkInBytes;

    @SerializedName("NetworkOut")
    private int networkOutBytes;

    @SerializedName("Restarting")
    private boolean restarting;

    @SerializedName("SaveCreatedTime")
    private String worldCreatedAt;

    public String getHostName()
    {
        return hostName;
    }

    public int getMaxPlayers()
    {
        return maxPlayers;
    }

    public int getCurrentPlayers()
    {
        return currentPlayers;
    }

    public int getQueuedPlayers()
    {
        return queuedPlayers;
    }

    public int getJoiningPlayers()
    {
        return joiningPlayers;
    }

    public int getEntityCount()
    {
        return entityCount;
    }

    public int getUptimeSeconds()
    {
        return uptimeSeconds;
    }

    public String getMap()
    {
        return map;
    }

    public double getFrameRate()
    {
        return frameRate;
    }

    public int getMemoryUsageMb()
    {
        return memoryUsageMb;
    }

    public int getCollections()
    {
        return collections;
    }

    public int getNetworkInBytes()
    {
        return networkInBytes;
    }

    public int getNetworkOutBytes()
    {
        return networkOutBytes;
    }

    public boolean isRestarting()
    {
        return restarting;
    }

    public String getWorldCreatedAt()
    {
        return worldCreatedAt;
    }
}
