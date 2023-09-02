package io.graversen.rust.rcon;

import lombok.NonNull;

public class TestRustRconResponse extends RustRconResponse {
    public TestRustRconResponse(@NonNull String message) {
        super(0, message, "type", testServer());
    }

    private static RustServer testServer() {
        return new SimpleRustServer("test", "ws://test");
    }
}
