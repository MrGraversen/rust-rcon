package io.graversen.rust.rcon;

public class RustRcon
{
    public static void main(String[] args)
    {
        IRconClient rconClient = RconClient.connect("graversen.io", "thegame159!", 30204);
        rconClient.sendRaw("say test 1");
        rconClient.sendRaw("say test 2");
        rconClient.sendRaw("say test 3");
        rconClient.sendRaw("playerlist");
        rconClient.sendRaw("env.time 900");
        rconClient.sendRaw("global.maxthreads");
        rconClient.sendRaw("<color=red>Test message</color>");
//        rconClient.sendRaw("inventory.giveto pope stones 100000000");
//        rconClient.sendRaw("inventory.giveto pope metal.refined 100000000");
//        rconClient.sendRaw("inventory.giveto pope metal.fragments 100000000");
//        rconClient.sendRaw("inventory.giveto pope wood 100000000");

    }
}
