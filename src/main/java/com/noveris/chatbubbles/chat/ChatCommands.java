package com.noveris.chatbubbles.chat;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import com.noveris.chatbubbles.NoverisChatBubbles;

public final class ChatCommands {
    private ChatCommands() {}
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        var local = Commands.literal("local").then(Commands.argument("message", StringArgumentType.greedyString())
                .executes(c -> local(c.getSource(), StringArgumentType.getString(c, "message"))));
        dispatcher.register(local);
        dispatcher.register(Commands.literal("l").redirect(dispatcher.getRoot().getChild("local")));
        var global = Commands.literal("global").then(Commands.argument("message", StringArgumentType.greedyString())
                .executes(c -> global(c.getSource(), StringArgumentType.getString(c, "message"))));
        dispatcher.register(global);
        dispatcher.register(Commands.literal("g").redirect(dispatcher.getRoot().getChild("global")));
    }
    private static int local(CommandSourceStack source, String message) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        NoverisChatBubbles.LOGGER.info("Executing /local for {}", player.getUUID());
        ChatManager.broadcastLocal(player, message);
        return 1;
    }
    private static int global(CommandSourceStack source, String message) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException(); ChatManager.broadcastGlobal(player, message); return 1;
    }
}
