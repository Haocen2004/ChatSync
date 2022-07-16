package xyz.hellocraft.chatSync.mc;

import net.minecraft.network.message.MessageType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;

public class MinecraftChatSender {

    private static MinecraftChatSender INSTANCE;
    private MinecraftServer server;

    public MinecraftChatSender(MinecraftServer server) {
        this.server = server;
    }

    public static MinecraftChatSender getINSTANCE(MinecraftServer server) {
        if (INSTANCE == null) {
            if (server == null) {
                throw new RuntimeException("Server NOT available!");
            }
            INSTANCE =  new MinecraftChatSender(server);
        }
        return INSTANCE;
    }

    public void sendMessage(String sender, String msg) {

        server.getPlayerManager().broadcast(Text.of("[KooK]<"+sender+"> "+msg),MessageType.SYSTEM);
    }
}
