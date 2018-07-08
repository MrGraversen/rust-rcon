package io.graversen.rust.rcon;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.graversen.rust.rcon.listeners.IConsoleListener;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class RconClient extends WebSocketClient implements IRconClient {
    private static final int DEFAULT_PORT = 25575;

    private final String connectionTuple;
    private final AtomicInteger currentRequestCounter;
    private final ExecutorService executorService;
    private final Rcon rcon;
    private final Gson gson;

    private final List<IConsoleListener> consoleListeners;

    private RconClient(URI uri) {
        super(uri);
        this.connectionTuple = String.format("%s:%d", uri.getHost(), uri.getPort());
        this.currentRequestCounter = new AtomicInteger(1);
        this.executorService = Executors.newSingleThreadExecutor();
        this.rcon = new Rcon(this);
        this.consoleListeners = new ArrayList<>();

        GsonBuilder gsonBuilder = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting();
        this.gson = gsonBuilder.create();

        try {
            connectBlocking();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        printLog(String.format("Initialized: %s", connectionTuple));
    }

    public Rcon rcon() {
        return rcon;
    }

    public static RconClient connect(String hostname, String password) {
        return RconClient.connect(hostname, password, RconClient.DEFAULT_PORT);
    }

    public static RconClient connect(String hostname, String password, int port) {
        try {
            return new RconClient(new URI(String.format("ws://%s:%d/%s", hostname, port, password)));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendRaw(String command) {
        final int identifier = currentRequestCounter.getAndIncrement();
        final WsRequest wsRequest = new WsRequest(identifier, command, "rust-rcon");
        final String json = gson.toJson(wsRequest);
        printLog("Sending: %s", json);

        send(json);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        printLog("Open: %d %s", handshakedata.getHttpStatus(), handshakedata.getHttpStatusMessage());
    }

    @Override
    public void onMessage(String message) {
        printLog("Received: %s", message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        printLog("Closed: %d %s", code, reason);
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }

    @Override
    public void attachConsoleListener(IConsoleListener consoleListener) {
        this.consoleListeners.add(consoleListener);
    }

    private void printLog(String logText, Object... args) {
        final String formattedLogText = String.format(logText, args);
        System.out.println(String.format("[RconClient]: %s", formattedLogText));
        consoleListeners.forEach(consoleListener -> consoleListener.onConsoleMessage(formattedLogText));
    }

    private void printCommand(String command) {
        printLog(String.format("Piping command: %s", command));
    }
}