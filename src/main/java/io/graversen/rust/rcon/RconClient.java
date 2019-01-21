//package io.graversen.rust.rcon;
//
//import com.google.gson.JsonSyntaxException;
//import io.graversen.rust.rcon.events.types.game.WorldEvent;
//import io.graversen.rust.rcon.events.types.player.ChatMessageEvent;
//import io.graversen.rust.rcon.events.types.player.PlayerConnectedEvent;
//import io.graversen.rust.rcon.events.types.player.PlayerDeathEvent;
//import io.graversen.rust.rcon.events.types.player.PlayerDisconnectedEvent;
//import io.graversen.rust.rcon.listeners.IConsoleListener;
//import io.graversen.rust.rcon.listeners.IServerEventListener;
//import io.graversen.rust.rcon.objects.rust.Player;
//import io.graversen.rust.rcon.objects.RconReceive;
//import io.graversen.rust.rcon.objects.RconRequest;
//import io.graversen.trunk.io.serialization.interfaces.ISerializer;
//import io.graversen.trunk.io.serialization.json.GsonSerializer;
//import org.java_websocket.client.WebSocketClient;
//import org.java_websocket.handshake.ServerHandshake;
//
//import java.net.URI;
//import java.net.URISyntaxException;
//import java.util.*;
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.atomic.AtomicInteger;
//import java.util.function.Consumer;
//
//public class RconClient extends WebSocketClient implements IRconClient, AutoCloseable
//{
//    private static final int DEFAULT_PORT = 25575;
//
//    private final String connectionTuple;
//    private final AtomicInteger currentRequestCounter;
//    private final Rcon rcon;
//    private final ISerializer serializer;
//
//    private final List<IConsoleListener> consoleListeners;
//    private final List<IServerEventListener> serverEventListeners;
//    private final Map<Integer, CompletableFuture<RconReceive>> asyncRequests;
//
//    private final DefaultConsoleParser defaultConsoleParser;
//
//    private RconClient(URI uri)
//    {
//        super(uri);
//
//        this.serializer = new GsonSerializer();
//        this.connectionTuple = String.format("%s:%d", uri.getHost(), uri.getPort());
//        this.currentRequestCounter = new AtomicInteger(1);
//        this.rcon = new Rcon(this, this.serializer);
//        this.consoleListeners = new ArrayList<>();
//        this.serverEventListeners = new ArrayList<>();
//        this.asyncRequests = new ConcurrentHashMap<>();
//        this.defaultConsoleParser = new DefaultConsoleParser();
//
//        printLog("Initialized: %s", connectionTuple);
//    }
//
//    public static RconClient connect(String hostname, String password) throws RconException
//    {
//        return RconClient.connect(hostname, password, RconClient.DEFAULT_PORT);
//    }
//
//    public static RconClient connect(String hostname, String password, int port) throws RconException
//    {
//        return connect(hostname, password, port, new ArrayList<>(), new ArrayList<>());
//    }
//
//    public static RconClient connect(String hostname, String password, int port, List<IConsoleListener> consoleListeners, List<IServerEventListener> serverEventListeners) throws RconException
//    {
//        try
//        {
//            final String connectionUri = String.format("ws://%s:%d/%s", hostname, port, password);
//            final String connectionUriMasked = String.format("ws://%s:%d/******", hostname, port);
//
//            final RconClient rconClient = new RconClient(new URI(connectionUri));
//
//            consoleListeners.forEach(rconClient::attachConsoleListener);
//            serverEventListeners.forEach(rconClient::attachServerEventListener);
//            rconClient.attachDefaultListeners();
//
//            final boolean connected = rconClient.connectBlocking();
//
//            if (!connected)
//            {
//                throw new RconException(String.format("Could not connect to %s", connectionUriMasked));
//            }
//
//            return rconClient;
//        }
//        catch (URISyntaxException | InterruptedException e)
//        {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @Override
//    public Rcon rcon()
//    {
//        return rcon;
//    }
//
//    @Override
//    public List<Player> getCurrentPlayers()
//    {
//        final Optional<RconReceive> wsObjectOptional = sendRawAndWait("playerlist", 5000);
//        final List<Player> playerList = new ArrayList<>();
//
//        wsObjectOptional.ifPresent(rconReceive ->
//        {
//            Player[] players = serializer.deserialize(rconReceive.getMessage(), Player[].class);
//            playerList.addAll(Arrays.asList(players));
//        });
//
//        return playerList;
//    }
//
//    private synchronized Optional<RconReceive> sendRawAndWait(String command, int timeout)
//    {
//        final CompletableFuture<RconReceive> wsObjectFuture = new CompletableFuture<>();
//        final int identifier = sendRaw(command);
//
//        asyncRequests.put(identifier, wsObjectFuture);
//
//        try
//        {
//            return Optional.ofNullable(wsObjectFuture.get(timeout, TimeUnit.MILLISECONDS));
//        }
//        catch (Exception e)
//        {
//            return Optional.empty();
//        }
//    }
//
//    @Override
//    public int sendRaw(String command)
//    {
//        final int identifier = currentRequestCounter.getAndIncrement();
//        final RconRequest rconRequest = new RconRequest(identifier, command, "rust-rcon");
//        final String json = serializer.serialize(rconRequest);
//        printLog("Sending: %s", json);
//        send(json);
//
//        return identifier;
//    }
//
//    @Override
//    public void onOpen(ServerHandshake handshakedata)
//    {
//        printLog("Open: %d %s", handshakedata.getHttpStatus(), handshakedata.getHttpStatusMessage());
//        serverEventListeners.forEach(IServerEventListener::onRconOpen);
//    }
//
//    @Override
//    public void onMessage(String message)
//    {
//        printLog(message);
//        consoleListeners.forEach(consoleListener -> consoleListener.onConsoleMessage(message));
//    }
//
//    @Override
//    public void onClose(int code, String reason, boolean remote)
//    {
//        printLog("Closed: %d %s", code, reason);
//        serverEventListeners.forEach(serverEventListener -> serverEventListener.onRconClosed(code, reason));
//    }
//
//    @Override
//    public void onError(Exception ex)
//    {
//        serverEventListeners.forEach(serverEventListener -> serverEventListener.onRconError(ex));
//    }
//
//    @Override
//    public void attachConsoleListener(IConsoleListener consoleListener)
//    {
//        this.consoleListeners.add(consoleListener);
//    }
//
//    @Override
//    public void attachServerEventListener(IServerEventListener serverEventListener)
//    {
//        this.serverEventListeners.add(serverEventListener);
//    }
//
//    private void printLog(String logText, Object... args)
//    {
//        final String formattedLogText = String.format(logText, args);
//        System.out.println(String.format("[%s]: %s", Constants.projectName(), formattedLogText));
//    }
//
//    private void printCommand(String command)
//    {
//        printLog("Piping command: %s", command);
//    }
//
//    private void attachDefaultListeners()
//    {
//        attachConsoleListener(asyncRequestsListener());
//        attachConsoleListener(serverEventDigestListener());
//    }
//
//    private IConsoleListener asyncRequestsListener()
//    {
//        return consoleMessage ->
//        {
//            final Optional<RconReceive> wsObjectOptional = tryDeserialize(consoleMessage);
//            wsObjectOptional.ifPresent(wsOutgoingObject ->
//            {
//                if (asyncRequests.containsKey(wsOutgoingObject.getIdentifier()))
//                {
//                    asyncRequests.get(wsOutgoingObject.getIdentifier()).complete(wsOutgoingObject);
//                }
//            });
//        };
//    }
//
//    private IConsoleListener serverEventDigestListener()
//    {
//        return consoleMessage ->
//        {
//            final Optional<RconReceive> wsObjectOptional = tryDeserialize(consoleMessage);
//            wsObjectOptional.ifPresent(
//                    wsOutgoingObject -> defaultConsoleParser.parse(consoleMessage).ifPresent(propagateConsoleDigest(consoleMessage))
//            );
//        };
//    }
//
//    private Consumer<RconMessageTypes> propagateConsoleDigest(String consoleMessage)
//    {
//        return consoleDigest ->
//        {
//            try
//            {
//                switch (consoleDigest)
//                {
//                    case CHAT:
//                        final ChatMessageEvent chatMessageEvent = defaultConsoleParser.parseChatMessageEvent(consoleMessage);
//                        serverEventListeners.forEach(serverEventListener -> serverEventListener.onChatMessage(chatMessageEvent));
//                        break;
//                    case PLAYER_CONNECTED:
//                        final PlayerConnectedEvent playerConnectedEvent = defaultConsoleParser.parsePlayerConnectedEvent(consoleMessage);
//                        serverEventListeners.forEach(serverEventListener -> serverEventListener.onPlayerConnected(playerConnectedEvent));
//                        break;
//                    case PLAYER_DISCONNECTED:
//                        final PlayerDisconnectedEvent playerDisconnectedEvent = defaultConsoleParser.parsePlayerDisconnectedEvent(consoleMessage);
//                        serverEventListeners.forEach(serverEventListener -> serverEventListener.onPlayerDisconnected(playerDisconnectedEvent));
//                        break;
//                    case PLAYER_DEATH:
//                        final PlayerDeathEvent playerDeathEvent = defaultConsoleParser.parsePlayerDeathEvent(consoleMessage);
//                        serverEventListeners.forEach(serverEventListener -> serverEventListener.onPlayerDeath(playerDeathEvent));
//                        break;
//                    case WORLD_EVENT:
//                        final WorldEvent worldEvent = defaultConsoleParser.parseWorldEvent(consoleMessage);
//                        serverEventListeners.forEach(serverEventListener -> serverEventListener.onWorldEvent(worldEvent));
//                        break;
//                }
//            }
//            catch (Exception e)
//            {
//                serverEventListeners.forEach(serverEventListener -> serverEventListener.onEventParseError(e));
//            }
//        };
//    }
//
//    private Optional<RconReceive> tryDeserialize(String consoleMessage)
//    {
//        try
//        {
//            final RconReceive wsOutgoingObject = serializer.deserialize(consoleMessage, RconReceive.class);
//            return Optional.of(wsOutgoingObject);
//        }
//        catch (JsonSyntaxException e)
//        {
//            return Optional.empty();
//        }
//    }
//}