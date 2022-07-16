package xyz.hellocraft.chatSync.kook;

import io.github.kookybot.events.EventHandler;
import io.github.kookybot.events.Listener;
import io.github.kookybot.events.channel.ChannelMessageEvent;
import xyz.hellocraft.chatSync.ChatSync;
import xyz.hellocraft.chatSync.mc.MinecraftChatSender;

public class ChannelChatListener implements Listener {

    @EventHandler
    // Received Channel Message Event / 收到频道消息事件
    public void onChannelMessage(ChannelMessageEvent event) {
        // Add a listener for channel messages / 添加一个监听器以侦听频道消息
        if (event.getChannel().getName().contains("服务器消息")) {
            if (event.getContent().startsWith("/")) return; // ignore command
            ChatSync.LOGGER.info("nickname:"+event.getSender().getNickname());
            ChatSync.LOGGER.info("name:"+event.getSender().getName());
            ChatSync.LOGGER.info("fullname:"+event.getSender().getFullName());
            MinecraftChatSender.getINSTANCE(null).sendMessage(event.getSender().getFullName(), event.getContent());
        }
    }
}
