package com.envyful.api.platform;

import com.envyful.api.config.ConfigToast;
import com.envyful.api.config.type.ConfigItem;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.scanning.PlatformScanning;
import com.envyful.api.text.Placeholder;

import java.util.Collection;
import java.util.List;

/**
 *
 * An interface designed to abstract the platform specific code from the rest of the plugin/mod
 * <br>
 * This prevents breaking changes from Mojang and other platforms from affecting the rest of the code
 * and allowing for the plugin/mod to be ported to other platforms with minimal changes and protected
 * from breaking changes between versions
 *
 * @param <A> The player type
 */
public interface PlatformHandler<A> {

    /**
     *
     * Check if a player has a permission
     *
     * @param player The player to check
     * @param permission The permission to check
     * @return True if they have the permission
     */
    boolean hasPermission(A player, String permission);

    /**
     *
     * Check if a player is an operator
     *
     * @param player The player to check
     * @return True if they are an operator
     */
    boolean isOP(A player);

    /**
     *
     * Broadcast a message to all players on the server with placeholders
     *
     * @param message The messages to broadcast
     * @param placeholders The placeholders to replace
     */
    void broadcastMessage(Collection<String> message, Placeholder... placeholders);

    /**
     *
     * Send a message to a player with placeholders
     *
     * @param player The player to send the message to
     * @param message The message to send
     * @param placeholders The placeholders to replace
     */
    void sendMessage(A player, Collection<String> message, Placeholder... placeholders);

    /**
     *
     * Checks if the current thread is the server thread
     *
     * @return If the current thread is the server thread
     */
    boolean isServerThread();

    /**
     *
     * Execute a runnable on the main server's thread
     *
     * @param runnable The runnable to execute
     */
    void runSync(Runnable runnable);

    /**
     *
     * Execute a runnable on the main server's thread after a delay
     *
     * @param runnable The runnable to execute
     * @param delayTicks The delay in ticks
     */
    void runLater(Runnable runnable, int delayTicks);

    /**
     *
     * Gets the current TPS of the server
     *
     * @return The current TPS
     */
    double getTPS();

    /**
     *
     * Exceutes the commands as the console
     *
     * @param commands The commands to execute
     * @param placeholders The placeholders to replace
     */
    void executeConsoleCommands(List<String> commands, Placeholder... placeholders);

    /**
     *
     * Sends a toast to the player
     *
     * @param player The player to send the toast to
     * @param configToast The toast to send
     */
    void sendToast(A player, ConfigToast configToast);

    /**
     *
     * Sends a toast to the player
     *
     * @param player The player to send the toast to
     * @param configToast The toast to send
     */
    void sendToast(EnvyPlayer<A> player, ConfigToast configToast);

    /**
     *
     * Checks if an item stack is a specific item
     *
     * @param itemStack The item stack to check
     * @param item The item to check against
     * @return True if the item stack is the same as the item
     */
    boolean isItem(Object itemStack, ConfigItem item);

    /**
     *
     * Gets the platform scanning instance
     *
     * @return The platform scanning instance
     */
    PlatformScanning scanning();
}
