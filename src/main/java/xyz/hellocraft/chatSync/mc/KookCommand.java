package xyz.hellocraft.chatSync.mc;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import xyz.hellocraft.chatSync.ChatSync;

import static net.minecraft.server.command.CommandManager.literal;

public class KookCommand implements Command<Object> {
    @Override
    public int run(CommandContext<Object> context) throws CommandSyntaxException {
        return 0;
    }

    public void register(CommandDispatcher<ServerCommandSource> dispatcher){
        dispatcher.register(
                literal("kook")
                        .then(literal("bind")
                                .then(literal("confirm").executes(
                                        context -> {
                                            if (!context.getSource().isExecutedByPlayer()) {
                                                context.getSource().sendError(Text.of("该命令只能由玩家执行！"));
                                                return 0;
                                            }
                                            switch (ChatSync.CONFIG.getOrDefault("bind."+context.getSource().getPlayer().getUuidAsString(),0)) {
                                                case 1: // waiting for confirm
                                                    // TODO
                                                case 2: // already bind
                                                    context.getSource().sendError(Text.of("你已经绑定了一个账号！"));
                                                    return 1;
                                                default:
                                                    context.getSource().sendError(Text.of("当前没有绑定请求"));
                                                    return 1;
                                            }
                                        }
                                )))
        );
    }
}
