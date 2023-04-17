package io.graversen.v1.rust.rcon.websocket;

import org.java_websocket.client.WebSocketClient;

import java.net.URI;

public abstract class BaseWebSocketClient extends WebSocketClient implements IWebSocketClient
{
    protected static final String WS_URI_TEMPLATE = "ws://%s:%d/%s";
    protected static final String WS_URI_TEMPLATE_MASKED = "ws://%s:%d/******";

    public BaseWebSocketClient(URI serverUri)
    {
        super(serverUri);
    }
}
