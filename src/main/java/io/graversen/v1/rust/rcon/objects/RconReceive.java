package io.graversen.v1.rust.rcon.objects;

import com.google.gson.annotations.SerializedName;

public class RconReceive
{
    @SerializedName("Identifier")
    private int identifier;
    @SerializedName("Message")
    private String message;
    @SerializedName("Type")
    private String type;
    @SerializedName("Stacktrace")
    private String stracktrace;

    public int getIdentifier()
    {
        return identifier;
    }

    public String getMessage()
    {
        return message;
    }

    public String getType()
    {
        return type;
    }

    public String getStracktrace()
    {
        return stracktrace;
    }
}
