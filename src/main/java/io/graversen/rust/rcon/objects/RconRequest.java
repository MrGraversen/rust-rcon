package io.graversen.rust.rcon.objects;

import com.google.gson.annotations.SerializedName;

public class RconRequest
{
    @SerializedName("Identifier")
    private int identifier;
    @SerializedName("Message")
    private String message;
    @SerializedName("Name")
    private String name;

    public RconRequest(int identifier, String message, String name)
    {
        this.identifier = identifier;
        this.message = message;
        this.name = name;
    }

    public int getIdentifier()
    {
        return identifier;
    }

    public String getMessage()
    {
        return message;
    }

    public String getName()
    {
        return name;
    }
}
