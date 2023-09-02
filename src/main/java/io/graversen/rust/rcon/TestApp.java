package io.graversen.rust.rcon;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import io.graversen.rust.rcon.event.AutoConfiguringRustEventService;
import io.graversen.rust.rcon.event.rcon.RconDebugSubscriber;
import io.graversen.rust.rcon.event.ws.WsDebugSubscriber;
import io.graversen.rust.rcon.protocol.dto.RustDtoMappers;
import io.graversen.rust.rcon.util.DefaultJsonMapper;
import io.graversen.rust.rcon.ws.ReconnectingRustWebSocketClient;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

@Slf4j
public class TestApp {
    @SneakyThrows
    public static void main(String[] args) {
        final var testApp = new TestApp();
        testApp.theRealBigTest();
        Thread.sleep(Duration.ofHours(1).toMillis());
    }

    @SneakyThrows
    public void startStopTest() {
        final var configuration = new RustRconConfiguration("65.108.204.35", 28016, "a7ef1abc-f553-11ec-a5b1-0f59e79878dc");
        final var rustRconService = new DefaultRustRconService(configuration);
        rustRconService.start();
        rustRconService.stop();
    }

    @SneakyThrows
    public void theRealBigTest() {
        final var configuration = new RustRconConfiguration("65.108.204.35", 28016, "a7ef1abc-f553-11ec-a5b1-0f59e79878dc");
        final var rustRconService = new DefaultRustRconService(configuration);
        rustRconService.enableRconLogger();
        rustRconService.start();

        final var rustMappers = new RustDtoMappers(new DefaultJsonMapper());
        final var serverInfo = rustRconService.codec().send(() -> "serverinfo", rustMappers.mapServerInfo()).get();
        log.info("It just works...");
    }

    @SneakyThrows
    public void theBigTest() {
        final var eventBus = new EventBus();
        final var eventService = new AutoConfiguringRustEventService(eventBus);
        eventService.configure();
        final var client = new ReconnectingRustWebSocketClient("65.108.204.35", 28016, "a7ef1abc-f553-11ec-a5b1-0f59e79878dc", eventBus);
        client.registerEvents(new WsDebugSubscriber());
        final var rustClient = new DefaultRustRconClient(client, eventBus);
        final var router = new DefaultRustRconRouter(rustClient, eventBus);
        router.registerEvents(new RconDebugSubscriber());
        router.start();

        final var rustMappers = new RustDtoMappers(new DefaultJsonMapper());

        final var serverInfo = router.send(() -> "serverinfo", rustMappers.mapServerInfo()).get();
        final var bans = router.send(() -> "bans", rustMappers.mapBans()).get();
        final var buildInfo = router.send(() -> "buildinfo", rustMappers.mapBuildInfo()).get();
        log.info("YEAH BOI!");
    }

    public void event() {
        final var eventBus = new EventBus();
        final var eventService = new AutoConfiguringRustEventService(eventBus);
        eventService.configure();
        final var client = new ReconnectingRustWebSocketClient("65.108.204.35", 28016, "a7ef1abc-f553-11ec-a5b1-0f59e79878dc", eventBus);
        client.registerEvents(new WsDebugSubscriber());
        final var rustClient = new DefaultRustRconClient(client, eventBus);
        final var router = new DefaultRustRconRouter(rustClient, eventBus);
        router.registerEvents(new RconDebugSubscriber());
        router.start();
    }

    @SneakyThrows
    public void router() {
        final var eventBus = new EventBus();
        final var client = new ReconnectingRustWebSocketClient("65.108.204.35", 28016, "a7ef1abc-f553-11ec-a5b1-0f59e79878dc", eventBus);
        client.registerEvents(new WsDebugSubscriber());
        final var rustClient = new DefaultRustRconClient(client, eventBus);
        final var router = new DefaultRustRconRouter(rustClient, eventBus);
        router.registerEvents(new RconDebugSubscriber());
        router.start();

        while (!client.isOpen()) {
            log.debug("Waiting for WS");
            Thread.sleep(10);
        }

        final var response1 = router.send(() -> "oxide.version");
        response1.whenComplete((rustRconResponse, throwable) -> log.info("Oh damn 1: {}", rustRconResponse));
//        final var response2 = router.send(() -> "inventory.giveto doc wood 1");
//        response2.whenComplete((rustRconResponse, throwable) -> log.info("Oh damn 2: {}", rustRconResponse));
//
        for (int i = 0; i < 10; i++) {
            router.send(() -> "oxide.version");
        }


    }

    public void eventBus() {
        final var eventBus = new EventBus();
        eventBus.register(this);
        eventBus.post(new MyEvent());
        eventBus.post(new MyEvent());
    }

    @Subscribe
    public void onMyEvent1(MyEvent myEvent) {
        log.info("Woah! MyEvent just happened! Would you believe it?");
    }

    @Subscribe
    public void onMyEvent2(MyEvent myEvent) {
        throw new RuntimeException("Damn");
    }

    public static class MyEvent {

    }
}
