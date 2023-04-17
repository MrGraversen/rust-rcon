package io.graversen.v1.rust.rcon.websocket;

public interface IWebSocketListener
{
    void onOpen();

    void onMessage(String message);

    void onClose(int code, String reason);

    void onError(Exception e);
}
