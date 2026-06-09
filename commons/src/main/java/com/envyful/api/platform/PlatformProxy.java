package com.envyful.api.platform;

import com.envyful.api.concurrency.UtilLogger;
import com.envyful.api.config.ConfigToast;
import com.envyful.api.config.database.DatabaseDetailsRegistry;
import com.envyful.api.config.type.ConfigItem;
import com.envyful.api.platform.text.TextFormatter;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.player.PlayerManager;
import com.envyful.api.scanning.PlatformScanning;
import com.envyful.api.text.Placeholder;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

/**
 *
 * Static utility class for handling platform specific code
 * and abstracting it from the rest of the plugin
 * <br>
 * This prevents breaking changes from Mojang and other platforms from affecting the rest of the code
 *
 */
public class PlatformProxy {

    static {
        DatabaseDetailsRegistry.init();
    }

    private static PlatformHandler handler;
    private static PlayerManager<?, ?> playerManager;
    private static TextFormatter<?> textFormatter;

    /**
     *
     * Sets the platform handler
     *
     * @param handler The handler
     */
    public static void setHandler(PlatformHandler handler) {
        PlatformProxy.handler = handler;
    }

    /**
     *
     * Sets the player manager
     *
     * @param playerManager The player manager
     */
    public static void setPlayerManager(PlayerManager<?, ?> playerManager) {
        PlatformProxy.playerManager = playerManager;
    }

    /**
     *
     * Sets the text formatter that will be used to parse and unparse text
     *
     * @param textFormatter The text formatter
     */
    public static void setTextFormatter(TextFormatter<?> textFormatter) {
        PlatformProxy.textFormatter = textFormatter;
    }

    /**
     *
     * Gets the player manager
     *
     * @return The player manager
     */
    public static PlayerManager<?, ?> getPlayerManager() {
        return playerManager;
    }

    /**
     *
     * Executes a console command
     *
     * @param commands The commands to execute
     */
    public static void executeConsoleCommands(String... commands) {
        executeConsoleCommands(List.of(commands));
    }

    /**
     *
     * Executes a console command
     *
     * @param commands The commands to execute
     */
    public static void executeConsoleCommands(List<String> commands, Placeholder... placeholders) {
        if (handler == null) {
            UtilLogger.getLogger().error("No platform handler set but executeConsoleCommands was called");
            return;
        }

        handler.executeConsoleCommands(commands, placeholders);
    }

    /**
     *
     * Checks if a player has a permission
     *
     * @param player The player
     * @param permission The permission
     * @return If the player has the permission
     */
    public static boolean hasPermission(Object player, String permission) {
        if (handler == null) {
            UtilLogger.getLogger().error("No platform handler set but hasPermission was called");
            return false;
        }

        return handler.hasPermission(player, permission);
    }

    /**
     *
     * Checks if a player has a permission
     *
     * @param player The player
     * @param permission The permission
     * @return If the player has the permission
     */
    public static boolean hasPermission(EnvyPlayer<?> player, String permission) {
        if (handler == null) {
            UtilLogger.getLogger().error("No platform handler set but hasPermission was called");
            return false;
        }

        return handler.hasPermission(player.getParent(), permission);
    }

    /**
     *
     * Checks if a player is an operator
     *
     * @param player The player
     * @return If the player is an operator
     */
    public static boolean isOP(EnvyPlayer<?> player) {
        if (handler == null) {
            UtilLogger.getLogger().error("No platform handler set but isOP was called");
            return false;
        }

        return handler.isOP(player.getParent());
    }

    /**
     *
     * Sends a toast to the player
     *
     * @param player The player to send the toast to
     * @param configToast The toast to send
     */
    public static void sendToast(Object player, ConfigToast configToast) {
        if (handler == null) {
            UtilLogger.getLogger().error("No platform handler set but sendToast was called");
            return;
        }

        handler.sendToast(player, configToast);
    }

    /**
     *
     * Sends a toast to the player
     *
     * @param player The player to send the toast to
     * @param configToast The toast to send
     */
    public static void sendToast(EnvyPlayer<?> player, ConfigToast configToast) {
        if (handler == null) {
            UtilLogger.getLogger().error("No platform handler set but sendToast was called");
            return;
        }

        handler.sendToast(player, configToast);
    }

    /**
     *
     * Broadcasts a message to the server
     *
     * @param message The message to broadcast
     * @param placeholders The placeholders to apply
     */
    public static void broadcastMessage(Collection<String> message, Placeholder... placeholders) {
        if (handler == null) {
            UtilLogger.getLogger().error("No platform handler set but broadcastMessage was called");
            return;
        }

        handler.broadcastMessage(message, placeholders);
    }

    /**
     *
     * Sends a message to the player
     *
     * @param player The player to send the message to
     * @param message The message to send
     */
    public static void sendMessage(Messageable<?> player, String... message) {
        sendMessage(player.getParent(), List.of(message));
    }

    /**
     *
     * Sends a message to the player
     *
     * @param player The player to send the message to
     * @param message The message to send
     */
    public static void sendMessage(Object player, String... message) {
        sendMessage(player, List.of(message));
    }

    /**
     *
     * Sends a message to the player
     *
     * @param player The player to send the message to
     * @param message The message to send
     */
    public static void sendMessage(Messageable<?> player, String message, Placeholder... placeholders) {
        sendMessage(player.getParent(), message, placeholders);
    }

    /**
     *
     * Sends a message to the player
     *
     * @param player The player to send the message to
     * @param message The message to send
     * @param placeholders The placeholders to apply
     */
    public static void sendMessage(Object player, String message, Placeholder... placeholders) {
        sendMessage(player, List.of(message), placeholders);
    }

    /**
     *
     * Sends a message to the player
     *
     * @param player The player to send the message to
     * @param message The message to send
     * @param placeholders The placeholders to apply
     */
    public static void sendMessage(Messageable<?> player, Collection<String> message, Placeholder... placeholders) {
        sendMessage(player.getParent(), message, placeholders);
    }

    /**
     *
     * Sends a message to the player
     *
     * @param player The player to send the message to
     * @param message The message to send
     * @param placeholders The placeholders to apply
     */
    public static void sendMessage(Object player, Collection<String> message, Placeholder... placeholders) {
        if (handler == null) {
            UtilLogger.getLogger().error("No platform handler set but sendMessage was called");
            return;
        }

        handler.sendMessage(player, message, placeholders);
    }

    /**
     *
     * Checks if the current thread is the platform's main thread
     *
     * @return If the current thread is the platform's main thread
     */
    public static boolean isServerThread() {
        if (handler == null) {
            UtilLogger.getLogger().error("No platform handler set but isServerThread was called");
            return false;
        }

        return handler.isServerThread();
    }

    /**
     *
     * Runs a task on the platform's main thread
     *
     * @param runnable The task to run
     */
    public static void runSync(Runnable runnable) {
        if (handler == null) {
            UtilLogger.getLogger().error("No platform handler set but runSync was called");
            return;
        }

        handler.runSync(runnable);
    }

    /**
     *
     * Runs a task after the given interval (in ticks) on the platform's main thread
     *
     * @param runnable The task to run
     */
    public static void runLater(Runnable runnable, int delayTicks) {
        if (handler == null) {
            UtilLogger.getLogger().error("No platform handler set but runLater was called");
            return;
        }

        handler.runLater(runnable, delayTicks);
    }

    /**
     *
     * Gets the current TPS (ticks per second) of the server from the platform handler
     *
     * @return The TPS
     */
    public static double getTPS() {
        if (handler == null) {
            UtilLogger.getLogger().error("No platform handler set but getTPS was called");
            return 0.0;
        }

        return handler.getTPS();
    }

    /**
     *
     * Checks if the item stack is the given config item
     *
     * @param itemStack The item stack to check
     * @param item The item to check against
     * @return If the item stack is an item
     */
    public static boolean isItem(Object itemStack, ConfigItem item) {
        if (handler == null) {
            UtilLogger.getLogger().error("No platform handler set but isItem was called");
            return false;
        }

        return handler.isItem(itemStack, item);
    }

    /**
     *
     * Parses the text provided
     *
     * @param text The text to parse
     * @return The parsed text
     * @param <T> The type of the object
     */
    public static <T> T parse(String text) {
        return (T) textFormatter.parse(text);
    }

    /**
     *
     * Parses the text provided into a list and also applies the placeholders
     *
     * @param text The text to parse
     * @param placeholders The placeholders to apply
     * @return The parsed text
     * @param <T> The type of the object
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> parse(String text, Placeholder... placeholders) {
        return (List<T>) textFormatter.parse(text, placeholders);
    }

    /**
     *
     * Parses the text provided into a list and also applies the placeholders
     *
     * @param text The text to parse
     * @param placeholders The placeholders to apply
     * @return The parsed text
     * @param <T> The type of the object
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> parse(Collection<String> text, Placeholder... placeholders) {
        return (List<T>) textFormatter.parse(text, placeholders);
    }

    /**
     *
     * Parses the text provided into a list and also applies the placeholders
     *
     * @param text The text to parse
     * @param placeholders The placeholders to apply
     * @return The parsed text
     * @param <T> The type of the object
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> parse(List<String> text, Placeholder... placeholders) {
        return (List<T>) textFormatter.parse(text, placeholders);
    }

    /**
     *
     * Parses text using the provided placeholders into a single object.
     * <br>
     * If the text is invalid, null is returned.
     * This will most likely happen when there are invalid placeholders in the text.
     *
     * @param text The text to parse
     * @param placeholders The placeholders
     * @param <T> The type of the object
     * @return The parsed object
     */
    public static <T> T flatParse(String text, Placeholder... placeholders) {
        List<T> parsed = parse(text, placeholders);

        if (parsed.isEmpty() || parsed.size() > 1) {
            return null;
        }

        return parsed.get(0);
    }

    /**
     *
     * Parses text using the provided placeholders into a single object.
     * <br>
     * If the text is invalid, the error handler is called (with the text as the parameter) and null is returned.
     * This will most likely happen when there are invalid placeholders in the text.
     *
     * @param text The text to parse
     * @param errorHandler The error handler
     * @param placeholders The placeholders
     * @param <T> The type of the object
     * @return The parsed objects
     */
    public static <T> T flatParse(String text, Consumer<String> errorHandler, Placeholder... placeholders) {
        List<T> parsed = parse(text, placeholders);

        if (parsed.isEmpty() || parsed.size() > 1) {
            errorHandler.accept(text);
            return null;
        }

        return parsed.get(0);
    }

    /**
     *
     * Unresolves an object to a string
     *
     * @param object The object to unresolve
     * @return The unresolved string
     * @param <T> The type of the object
     */
    @SuppressWarnings("unchecked")
    public static <T> String unresolve(T object) {
        return unresolve((TextFormatter<T>) textFormatter, object);
    }

    /**
     *
     * Unresolves an object to a string
     *
     * @param textFormatter The text formatter
     * @param object The object to unresolve
     * @param <T> The type of the object
     * @return The unresolved string
     */
    private static <T> String unresolve(TextFormatter<T> textFormatter, T object) {
        return textFormatter.unresolve(object);
    }

    /**
     *
     * Strips the colour codes from a string
     *
     * @param text The text to strip
     * @return The stripped text
     */
    public static String strip(String text) {
        return textFormatter.strip(text);
    }

    public static PlatformScanning scanning() {
        if (handler == null) {
            UtilLogger.getLogger().error("No platform handler set but scanning was called");
            return null;
        }

        return handler.scanning();
    }

}
