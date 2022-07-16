package xyz.hellocraft.chatSync.kook;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import io.github.kookybot.JavaBaseClass;
import io.github.kookybot.client.Client;
import io.github.kookybot.commands.CommandSource;
import io.github.kookybot.contract.Channel;
import io.github.kookybot.contract.Guild;
import io.github.kookybot.contract.Self;
import io.github.kookybot.contract.TextChannel;
import net.minecraft.network.message.MessageType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;



public class KookBot {
    private static KookBot INSTANCE;
    private Client client;
    private Self bot;
    private MinecraftServer server;
    public static KookBot getINSTANCE(MinecraftServer server) {
        if (INSTANCE == null) {
            INSTANCE = new KookBot(server);
        }
        return INSTANCE;
    }
    public static KookBot getINSTANCE() {
        if (INSTANCE == null) {
            throw new RuntimeException("Server NOT available!");
        }
        return INSTANCE;
    }
    public static LiteralArgumentBuilder<CommandSource> literal(String literal) {
        return LiteralArgumentBuilder.literal(literal);
    }

    public KookBot(MinecraftServer server) {
        this.server = server;
        client = new Client("token here", configure -> {
            // Register default Brigadier commands / 注册默认 Brigadier 命令
            configure.withDefaultCommands();
            return null;
        });


        bot = JavaBaseClass.utils.connectWebsocket(client);
        bot.getLogger().info("Kook Bot Connected.");
        // Add a listener for channel messages / 添加一个监听器以侦听频道消息

        CommandDispatcher<CommandSource> dispatcher =
                client.getEventManager().getDispatcher();

        dispatcher.register(
                (literal("bind"))
                        .then(
                                RequiredArgumentBuilder.argument("username", StringArgumentType.greedyString())
                        )
                        .executes(c -> {
                            ServerPlayerEntity player = server.getPlayerManager().getPlayer(StringArgumentType.getString(c, "username"));;
                            try {
                                player.sendMessage(Text.of("[KooK] 绑定用户中,请输入/kook bind confirm来确认"), MessageType.SYSTEM);
                                return 1;
                            } catch (Exception e) {
                                c.getSource().sendMessage("该玩家不在线，请检查用户名后再试");
                                return 0;
                            }
                        })
        );
        client.getEventManager().addClassListener(new ChannelChatListener());
        bot.getLogger().info("Kook Message Handle Created.");
    }

    public Self getBot() {
        return bot;
    }

    public Client getClient() {
        return client;
    }

    public void sendMessage(String msg) {
        new Thread(){
            @Override
            public void run() {
                super.run();
                for (Guild guild : bot.getGuilds()) {
                    if(guild.getName().contains("测试")) {
                        try {
                            for (Channel channel : guild.getChannels()) {
                                if (channel.getName().contains("服务器消息")) {
                                    ((TextChannel)channel).sendMessage(msg,null);
                                }
                            }
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }.start();
    }
}
