package io.graversen.rust.rcon;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.graversen.fiber.config.base.SimpleServerConfig;
import io.graversen.fiber.server.websocket.SimpleWebSocketServer;
import io.graversen.fiber.server.websocket.base.AbstractWebSocketServer;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.SocketChannel;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class RconClient extends WebSocketClient implements IRconClient
{
    private static final int DEFAULT_PORT = 25575;

    private final String connectionTuple;
    private final AtomicInteger currentRequestCounter;
    private final ExecutorService executorService;
    private final Rcon rcon;
    private final Gson gson;

    private RconClient(URI uri)
    {
        super(uri);
        this.connectionTuple = String.format("%s:%d", uri.getHost(), uri.getPort());
        this.currentRequestCounter = new AtomicInteger(1);
        this.executorService = Executors.newSingleThreadExecutor();
        this.rcon = new Rcon(this);

        GsonBuilder gsonBuilder = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting();
        this.gson = gsonBuilder.create();

        try
        {
            connectBlocking();
        }
        catch (InterruptedException e)
        {
            throw new RuntimeException(e);
        }

        printLog(String.format("Initialized: %s", connectionTuple));
    }

    public Rcon rcon()
    {
        return rcon;
    }

    public static RconClient connect(String hostname, String password)
    {
        return RconClient.connect(hostname, password, RconClient.DEFAULT_PORT);
    }

    public static RconClient connect(String hostname, String password, int port)
    {
        try
        {
            return new RconClient(new URI(String.format("ws://%s:%d/%s", hostname, port, password)));
        }
        catch (URISyntaxException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendRaw(String command)
    {
        final int identifier = currentRequestCounter.getAndIncrement();
        final WsRequest wsRequest = new WsRequest(identifier, command, "rust-rcon");
        final String json = gson.toJson(wsRequest);
        printLog("Sending: %s", json);

        send(json);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata)
    {
        printLog("Open: %d %s", handshakedata.getHttpStatus(), handshakedata.getHttpStatusMessage());
    }

    @Override
    public void onMessage(String message)
    {
        printLog("Received: %s", message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote)
    {
        printLog("Closed: %d %s", code, reason);
    }

    @Override
    public void onError(Exception ex)
    {
        ex.printStackTrace();
    }

    private void printLog(String logText, Object... args)
    {
        logText = String.format(logText, args);
        System.out.println(String.format("[RconClient]: %s", logText));
    }

    private void printCommand(String command)
    {
        printLog(String.format("Piping command: %s", command));
    }
}