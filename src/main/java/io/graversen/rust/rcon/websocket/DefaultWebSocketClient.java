package io.graversen.rust.rcon.websocket;

import io.graversen.rust.rcon.RconException;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

public class DefaultWebSocketClient extends BaseWebSocketClient implements IWebSocketClient
{
    private final IWebSocketListener webSocketListener;
    private final String connectionUri;
    private final String connectionUriMasked;

    public DefaultWebSocketClient(URI serverUri, IWebSocketListener webSocketListener)
    {
        super(serverUri);
        this.webSocketListener = webSocketListener;
        this.connectionUri = String.format(WS_URI_TEMPLATE, serverUri.getHost(), serverUri.getPort(), serverUri.getPath());
        this.connectionUriMasked = String.format(WS_URI_TEMPLATE_MASKED, serverUri.getHost(), serverUri.getPort());
    }

    public static DefaultWebSocketClient usingCredentialsAndListener(String hostname, String password, int port, IWebSocketListener webSocketListener)
    {
        try
        {
            final String uriCompiled = String.format(WS_URI_TEMPLATE, hostname, port, password);
            return new DefaultWebSocketClient(new URI(uriCompiled), webSocketListener);
        }
        catch (URISyntaxException e)
        {
            throw new RconException("Could not construct server URI");
        }
    }

    @Override
    public void close()
    {
        super.close(CLOSE_NORMAL);
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake)
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
    public boolean open()
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

    @Override
    public String connectionUriMasked()
    {
        return connectionUriMasked;
    }
}
