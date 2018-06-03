package io.graversen.rust.rcon;

import com.google.gson.annotations.SerializedName;

public class WsRequest
{
    @SerializedName("Identifier")
    private int identifier;
    @SerializedName("message")
    public String message;
    @SerializedName("name")
    public String name;

    public WsRequest(int identifier, String message, String name)
    {
        this.identifier = identifier;
        this.message = message;
        this.name = name;
    }
}
