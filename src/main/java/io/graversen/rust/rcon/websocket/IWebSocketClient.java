package io.graversen.rust.rcon.websocket;

public interface IWebSocketClient extends AutoCloseable
{
    int CLOSE_NORMAL = 1001;

    boolean open();

    void send(String payload);
}
