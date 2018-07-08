package io.graversen.rust.rcon;

import java.util.Optional;

public class RustRcon {
    public static void main(String[] args) {
        final IRconClient rconClient = RconClient.connect("graversen.io", Constants.RCON_PASSWORD, 30208);
        final ConsoleMessageDigester consoleMessageDigester = new ConsoleMessageDigester();

        rconClient.attachConsoleListener(consoleMessage -> {
            Optional<ConsoleDigests> digestsOptional = consoleMessageDigester.digest(consoleMessage);

            digestsOptional.ifPresent(consoleDigest -> {
                if (consoleDigest.equals(ConsoleDigests.CHAT)) {

                }
            });
        });

    }
}
