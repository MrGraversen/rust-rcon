package io.graversen.rust.rcon;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import io.graversen.rust.rcon.listeners.IConsoleListener;
import io.graversen.rust.rcon.listeners.IServerEventListener;
import io.graversen.rust.rcon.objects.rust.Player;
import io.graversen.rust.rcon.objects.ws.WsIngoingObject;
import io.graversen.rust.rcon.objects.ws.WsOutgoingObject;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class RconClient extends WebSocketClient implements IRconClient
{
    private static final int DEFAULT_PORT = 25575;

    private final String connectionTuple;
    private final AtomicInteger currentRequestCounter;
    private final Rcon rcon;
    private final Gson gson;

    private final List<IConsoleListener> consoleListeners;
    private final List<IServerEventListener> serverEventListeners;
    private final Map<Integer, CompletableFuture<WsIngoingObject>> asyncRequests;

    private RconClient(URI uri)
    {
        super(uri);

        GsonBuilder gsonBuilder = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting();
        this.gson = gsonBuilder.create();

        this.connectionTuple = String.format("%s:%d", uri.getHost(), uri.getPort());
        this.currentRequestCounter = new AtomicInteger(1);
        this.rcon = new Rcon(this, this.gson);
        this.consoleListeners = new ArrayList<>();
        this.serverEventListeners = new ArrayList<>();
        this.asyncRequests = new ConcurrentHashMap<>();

        printLog(false, "Initialized: %s", connectionTuple);
    }

    public static RconClient connect(String hostname, String password)
    {
        return RconClient.connect(hostname, password, RconClient.DEFAULT_PORT);
    }

    public static RconClient connect(String hostname, String password, int port)
    {
        try
        {
            final RconClient rconClient = new RconClient(new URI(String.format("ws://%s:%d/%s", hostname, port, password)));
            rconClient.connectBlocking();
            rconClient.attachDefaultListeners();

            return rconClient;
        }
        catch (URISyntaxException | InterruptedException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Rcon rcon()
    {
        return rcon;
    }

    @Override
    public List<Player> getCurrentPlayers()
    {
        final Optional<WsIngoingObject> wsObjectOptional = sendRawAndWait("playerlist", 5000);
        final List<Player> playerList = new ArrayList<>();

        wsObjectOptional.ifPresent(wsIngoingObject ->
        {
            Player[] players = gson.fromJson(wsIngoingObject.getMessage(), Player[].class);
            playerList.addAll(Arrays.asList(players));
        });

        return playerList;
    }

    private synchronized Optional<WsIngoingObject> sendRawAndWait(String command, int timeout)
    {
        final CompletableFuture<WsIngoingObject> wsObjectFuture = new CompletableFuture<>();
        final int identifier = sendRaw(command);

        asyncRequests.put(identifier, wsObjectFuture);

        try
        {
            return Optional.ofNullable(wsObjectFuture.get(timeout, TimeUnit.MILLISECONDS));
        }
        catch (Exception e)
        {
            return Optional.empty();
        }
    }

    @Override
    public int sendRaw(String command)
    {
        final int identifier = currentRequestCounter.getAndIncrement();
        final WsOutgoingObject wsOutgoingObject = new WsOutgoingObject(identifier, command, "rust-rcon");
        final String json = gson.toJson(wsOutgoingObject);
        printLog(false, "Sending: %s", json);
        send(json);

        return identifier;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata)
    {
        printLog(false, "Open: %d %s", handshakedata.getHttpStatus(), handshakedata.getHttpStatusMessage());
    }

    @Override
    public void onMessage(String message)
    {
        printLog(true, message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote)
    {
        printLog(false, "Closed: %d %s", code, reason);
    }

    @Override
    public void onError(Exception ex)
    {
        ex.printStackTrace();
    }

    @Override
    public void attachConsoleListener(IConsoleListener consoleListener)
    {
        this.consoleListeners.add(consoleListener);
    }

    @Override
    public void attachServerEventListener(IServerEventListener serverEventListener)
    {
        this.serverEventListeners.add(serverEventListener);
    }

    private void printLog(boolean propagate, String logText, Object... args)
    {
        final String formattedLogText = String.format(logText, args);
        System.out.println(String.format("[RconClient]: %s", formattedLogText));

        if (propagate)
        {
            consoleListeners.forEach(consoleListener -> consoleListener.onConsoleMessage(formattedLogText));
        }
    }

    private void printCommand(String command)
    {
        printLog(false, "Piping command: %s", command);
    }

    private void attachDefaultListeners()
    {
        attachConsoleListener(asyncRequestsListener());
        attachConsoleListener(serverEventDigestListener());
    }

    private IConsoleListener asyncRequestsListener()
    {
        return consoleMessage ->
        {
            final Optional<WsIngoingObject> wsObjectOptional = tryDeserialize(consoleMessage);
            wsObjectOptional.ifPresent(wsOutgoingObject ->
            {
                if (asyncRequests.containsKey(wsOutgoingObject.getIdentifier()))
                {
                    asyncRequests.get(wsOutgoingObject.getIdentifier()).complete(wsOutgoingObject);
                }
            });
        };
    }

    private IConsoleListener serverEventDigestListener()
    {
        return consoleMessage ->
        {
            final Optional<WsIngoingObject> wsObjectOptional = tryDeserialize(consoleMessage);
            wsObjectOptional.ifPresent(wsOutgoingObject ->
            {

            });
        };
    }

    private Optional<WsIngoingObject> tryDeserialize(String consoleMessage)
    {
        try
        {
            final WsIngoingObject wsOutgoingObject = gson.fromJson(consoleMessage, WsIngoingObject.class);
            return Optional.of(wsOutgoingObject);
        }
        catch (JsonSyntaxException e)
        {
            return Optional.empty();
        }
    }
}