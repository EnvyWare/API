package com.envyful.api.forge.server;

import com.envyful.api.platform.PlatformProxy;
import com.envyful.api.text.Placeholder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.Collection;
import java.util.List;

/**
 *
 * Static utility class for handling forge server function
 *
 * @deprecated Use {@link com.envyful.api.platform.PlatformProxy}
 */
@Deprecated
public class UtilForgeServer {

    /**
     *
     * Executes the given command from the server
     *
     * Ensure to set the server first
     *
     * @param command The command to execute
     */
    public static void executeCommand(String command) {
        if (ServerLifecycleHooks.getCurrentServer() == null || ServerLifecycleHooks.getCurrentServer().isShutdown()) {
            return;
        }

        if (!ServerLifecycleHooks.getCurrentServer().isSameThread()) {
            ServerLifecycleHooks.getCurrentServer().execute(() -> executeCommand(command));
            return;
        }

        ServerLifecycleHooks.getCurrentServer().getCommands().performPrefixedCommand(ServerLifecycleHooks.getCurrentServer().createCommandSourceStack(), command);
    }


    /**
     *
     * Executes the given command from the given player
     *
     * Ensure to set the server first
     *
     * @param player THe player to execute the command as
     * @param command The command to execute
     */
    public static void executeCommand(ServerPlayer player, String command) {
        ServerLifecycleHooks.getCurrentServer().getCommands().performPrefixedCommand(player.createCommandSourceStack(), command);
    }

    /**
     *
     * Broadcast the messages to all players online
     *
     * @param messages The messages
     */
    public static void broadcast(String... messages) {
        broadcast(List.of(messages));
    }

    /**
     *
     * Broadcast the messages to all players online
     *
     * @param messages The messages
     * @param placeholders Placeholders
     */
    public static void broadcast(Collection<String> messages, Placeholder... placeholders) {
        PlatformProxy.broadcastMessage(messages, placeholders);
    }

    /**
     *
     * Broadcast the messages to all players online
     *
     * @param messages The messages
     */
    public static void formattedBroadcast(Component... messages) {
        formattedBroadcast(List.of(messages));
    }

    /**
     *
     * Broadcast the messages to all players online
     *
     * @param messages The messages
     */
    public static void formattedBroadcast(Collection<Component> messages) {
        for (Component message : messages) {
            ServerLifecycleHooks.getCurrentServer().getPlayerList().broadcastSystemMessage(
                    message, true
            );
        }
    }
}
