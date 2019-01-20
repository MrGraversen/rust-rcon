package io.graversen.rust.rcon.websocket;

import java.net.URI;

public interface IWebSocketListener
{
    void onOpen();

    void onMessage(String message);

    void onClose(int code, String reason);

    void onError(Exception e);
}
