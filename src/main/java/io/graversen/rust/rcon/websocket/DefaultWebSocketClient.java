package io.graversen.rust.rcon.websocket;

import io.graversen.rust.rcon.RconException;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class DefaultWebSocketClient extends WebSocketClient implements IWebSocketClient
{
    private final IWebSocketListener webSocketListener;

    public DefaultWebSocketClient(URI serverUri, IWebSocketListener webSocketListener)
    {
        super(serverUri);
        this.webSocketListener = webSocketListener;
    }

    @Override
    public void close()
    {
        super.close(CLOSE_NORMAL);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata)
    {
        webSocketListener.onOpen();
    }

    @Override
    public void onMessage(String message)
    {
        webSocketListener.onMessage(message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote)
    {
        webSocketListener.onClose(code, reason);
    }

    @Override
    public void onError(Exception ex)
    {
        webSocketListener.onError(ex);
    }

    @Override
    public boolean connect(URI uri)
    {
        try
        {
            final boolean connected = super.connectBlocking();

            if (!connected)
            {
                this.onError(new RconException("Could not connect to remote server"));
            }

            return connected;
        }
        catch (InterruptedException e)
        {
            this.onError(e);
        }

        return false;
    }
}
