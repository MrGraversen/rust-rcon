package io.graversen.rust.rcon.websocket;

import java.net.URI;

public interface IWebSocketClient extends AutoCloseable
{
    int CLOSE_NORMAL = 1001;

    boolean connect(URI uri);
}
