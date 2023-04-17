package io.graversen.v1.rust.rcon.websocket;

public interface IWebSocketClient extends AutoCloseable
{
    int CLOSE_NORMAL = 1001;
    int CONNECTION_REFUSED = -1;

    boolean open();

    void send(String payload);

    String connectionUriMasked();
}
