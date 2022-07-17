package xyz.hellocraft.chatSync;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.hellocraft.chatSync.kook.KookBot;
import xyz.hellocraft.chatSync.mc.KookCommand;
import xyz.hellocraft.chatSync.mc.MinecraftChatSender;
import xyz.hellocraft.chatSync.mc.SimpleConfig;

public class ChatSync implements DedicatedServerModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("chatsync");
    public static final SimpleConfig CONFIG = SimpleConfig.getInstance();
    private KookBot bot;
    public static boolean ENABLE = false;

    @Override
    public void onInitializeServer() {

        LOGGER.info("Loading ChatSync...");


        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            MinecraftChatSender.getINSTANCE(server);


            LOGGER.info("Loading KookBot...");
            bot = KookBot.getINSTANCE(server);
        });

        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            if (ENABLE)
                bot.getClient().close();
        });

        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> {
            if (environment.dedicated) {
                new KookCommand().register(dispatcher);
            }
        }));


    }


}
