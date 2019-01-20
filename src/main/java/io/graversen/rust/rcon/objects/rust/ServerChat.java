package io.graversen.rust.rcon.objects.rust;

import com.google.gson.annotations.SerializedName;

public class ServerChat implements IChat
{
    @SerializedName("Message")
    private String message;

    @SerializedName("UserId")
    private String steamId;

    @SerializedName("Username")
    private String displayName;

    @SerializedName("Color")
    private String colorHex;

    @SerializedName("Time")
    private Long timestamp;

    @Override
    public String getMessage()
    {
        return message;
    }

    public String getSteamId()
    {
        return steamId;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public String getColorHex()
    {
        return colorHex;
    }

    public Long getTimestamp()
    {
        return timestamp;
    }
}
