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
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import xyz.hellocraft.chatSync.ChatSync;


public class KookBot {
    private static KookBot INSTANCE;
    private Client client;
    private Self bot;

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
        String token = String.valueOf(ChatSync.CONFIG.get("token"));
        if (token.equals("please insert your token here.") || token.equals("none")) {
            ChatSync.LOGGER.warn("TOKEN not set,please edit ChatSync.json and insert your token.");
            return;
        }

        client = new Client(String.valueOf(ChatSync.CONFIG.get("token")), configure -> {
            // Register default Brigadier commands / 注册默认 Brigadier 命令
            configure.withDefaultCommands();
            return null;
        });


        bot = JavaBaseClass.utils.connectWebsocket(client);
        ChatSync.LOGGER.info("Kook Bot Connected.");
        // Add a listener for channel messages / 添加一个监听器以侦听频道消息

        CommandDispatcher<CommandSource> dispatcher =
                client.getEventManager().getDispatcher();
        dispatcher.register(
                (literal("bind"))
                        .then(RequiredArgumentBuilder.argument("username", (ArgumentType) (StringArgumentType.greedyString()))

                                .executes(c -> {

                                    CommandSource commandSource = ((CommandSource) c.getSource());
                                    try {
                                        ServerPlayerEntity player = server.getPlayerManager().getPlayer(StringArgumentType.getString(c, "username"));
                                        player.sendMessage(Text.of("[KooK] 绑定用户中,请输入/kook bind confirm来确认"), MessageType.SYSTEM);
                                        ChatSync.CONFIG.put(player.getUuidAsString(), commandSource.getUser().getId());
                                        return 1;
                                    } catch (IllegalArgumentException e) {
                                        commandSource.sendMessage("输入 /bind <username> 来绑定账号");
                                        return 1;
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        commandSource.sendMessage("该玩家不在线或用户名错误！请检查后重试");
                                        return 0;
                                    }
                                })
                        ));
        dispatcher.register(
                (literal("unbind")
                        .executes(context -> {
                            CommandSource commandSource = context.getSource();
                            ChatSync.CONFIG.put(commandSource.getUser().getId(), "null");
                            commandSource.sendMessage("您已解除绑定");
                            return 1;
                        }))
        );
        dispatcher.register(
                (literal("list")
                        .executes(context -> {
                            StringBuilder ret = new StringBuilder("当前玩家列表：");
                            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                                ret.append(player.getName().getString()).append("\n");
                            }
                            context.getSource().getChannel().sendMessage(ret + "共 " + server.getPlayerManager().getPlayerList().size() + "人", null);
                            return 1;
                        }))
        );

        client.getEventManager().addClassListener(new ChannelChatListener());
        ChatSync.LOGGER.info("Kook Message Handle Created.");
        ChatSync.ENABLE = true;
        ChatSync.LOGGER.info("ChatSync loaded.");
    }

    public Client getClient() {
        return client;
    }

    public void sendMessage(String msg) {
        if (!ChatSync.ENABLE) return;
        new Thread(){
            @Override
            public void run() {
                super.run();
                for (Guild guild : bot.getGuilds()) {
                    if (guild.getName().contains("Hellocraft")) {
                        try {
                            for (Channel channel : guild.getChannels()) {
                                if (channel.getName().contains("服务器消息")) {
                                    ((TextChannel) channel).sendMessage(msg, null);
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
