package io.graversen.rust.rcon;

public enum ConsoleDigests implements IDigest {
    CHAT {
        @Override
        public boolean matches(String consoleInput) {
            return consoleInput.startsWith("[CHAT]");
        }
    },
    PLAYER_CONNECTED {
        @Override
        public boolean matches(String consoleInput) {
            return consoleInput.contains("joined [");
        }
    },
    PLAYER_DISCONNECTED {
        @Override
        public boolean matches(String consoleInput) {
            return consoleInput.contains("disconnecting:");
        }
    },
    PLAYER_DEATH {
        @Override
        public boolean matches(String consoleInput) {
            return false;
        }
    },
    SERVER_EVENT {
        @Override
        public boolean matches(String consoleInput) {
            return consoleInput.startsWith("[event]");
        }
    }
}
