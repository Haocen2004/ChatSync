package xyz.hellocraft.chatSync.mc;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import xyz.hellocraft.chatSync.ChatSync;

import static net.minecraft.server.command.CommandManager.literal;

public class KookCommand {

    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                literal("kook")
                        .then(literal("bind")
                                .then(literal("confirm").executes(
                                        context -> {
                                            if (!context.getSource().isExecutedByPlayer()) {
                                                context.getSource().sendError(Text.of("该命令只能由玩家执行！"));
                                                return 0;
                                            }
                                            try {
                                                String bindKookID = String.valueOf(ChatSync.CONFIG.get(context.getSource().getPlayer().getUuidAsString()));
                                                if (bindKookID.equals("null")) {
                                                    // no bind task
                                                    context.getSource().sendError(Text.of("你没有绑定账号的请求！请先在KooK端发起绑定"));
                                                    return 0;
                                                }
                                                if (!(bindKookID.length() >= 8)) {
                                                    context.getSource().sendError(Text.of("你已经绑定了一个账号！"));
                                                    return 0;
                                                } else {
                                                    context.getSource().sendFeedback(Text.of("success"), true);
                                                    ChatSync.CONFIG.put(bindKookID, context.getSource().getPlayer().getUuidAsString());
                                                    ChatSync.CONFIG.put(context.getSource().getPlayer().getUuidAsString(), "bind");
                                                    ChatSync.LOGGER.info("bind: " + bindKookID);
                                                    return 1;
                                                }
                                            } catch (NullPointerException e) {
                                                // no bind task
                                                context.getSource().sendError(Text.of("你没有绑定账号的请求！请现在KooK端发起绑定"));
                                                return 0;
                                            }
                                        }
                                )))
        );
    }
}
