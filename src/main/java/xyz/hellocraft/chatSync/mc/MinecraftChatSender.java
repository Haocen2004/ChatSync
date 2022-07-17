package xyz.hellocraft.chatSync.mc;

import io.github.kookybot.contract.GuildUser;
import net.minecraft.network.message.MessageSignature;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import xyz.hellocraft.chatSync.ChatSync;

import java.util.UUID;

public record MinecraftChatSender(MinecraftServer server) {

    private static MinecraftChatSender INSTANCE;

    public static MinecraftChatSender getINSTANCE(MinecraftServer server) {
        if (INSTANCE == null) {
            if (server == null) {
                throw new RuntimeException("Server NOT available!");
            }
            INSTANCE = new MinecraftChatSender(server);
        }
        return INSTANCE;
    }

    public void sendMessage(GuildUser sender, String msg) {
        String uuid = String.valueOf(ChatSync.CONFIG.get(sender.getId()));
        ChatSync.LOGGER.info(uuid);
        if (uuid.equals("null")) {
            server.getPlayerManager().broadcast(Text.of("[KooK]<" + sender.getName() + "> " + msg), MessageType.SYSTEM);
        } else {
            try {
                ServerPlayerEntity player = server.getPlayerManager().getPlayer(UUID.fromString(uuid));
                SignedMessage signedMessage = new SignedMessage(Text.of(msg), MessageSignature.none(), null).withUnsigned(Text.of(msg));
                server.getPlayerManager().broadcast(signedMessage,
                        player1 -> signedMessage,
                        player.asMessageSender(),
                        MessageType.CHAT);
            } catch (Exception e) {
//                e.printStackTrace();
                server.getPlayerManager().broadcast(Text.of("[KooK]<" + sender.getName() + "> " + msg), MessageType.SYSTEM);
            }
        }
    }
}
