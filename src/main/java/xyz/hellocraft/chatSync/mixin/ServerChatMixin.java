package xyz.hellocraft.chatSync.mixin;

import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.hellocraft.chatSync.kook.KookBot;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerChatMixin {

    @Shadow public ServerPlayerEntity player;

    @Inject(
            at = @At("HEAD"),
            method = "onChatMessage"
    )
    private void onChatMessage(ChatMessageC2SPacket packet, CallbackInfo ci) {
        packet.getChatMessage();
        KookBot.getINSTANCE().sendMessage("[Server]<"+player.getName().getString()+"> "+packet.getChatMessage());
    }

    @Inject(
            at = @At("HEAD"),
            method = "onDisconnected"
    )
    private void onDisconnected(Text reason, CallbackInfo ci){
        KookBot.getINSTANCE().sendMessage("[Server] "+player.getName().getString()+" left the game");
    }

    @Inject(method = "<init>*", at = @At("RETURN"))
    private void init(CallbackInfo info) {
        KookBot.getINSTANCE().sendMessage("[Server] "+player.getName().getString()+" joined the game");
    }

}
