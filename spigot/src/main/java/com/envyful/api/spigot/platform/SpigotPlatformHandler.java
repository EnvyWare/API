package com.envyful.api.spigot.platform;

import com.envyful.api.config.ConfigToast;
import com.envyful.api.config.type.ConfigItem;
import com.envyful.api.platform.PlatformHandler;
import com.envyful.api.platform.StandardPlatformHandler;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.scanning.NoOpPlatformScanning;
import com.envyful.api.scanning.PlatformScanning;
import com.envyful.api.text.Placeholder;
import com.envyful.api.text.PlaceholderFactory;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.List;

public class SpigotPlatformHandler extends StandardPlatformHandler<Audience> {

    protected final Plugin plugin;

    private SpigotPlatformHandler(Plugin plugin) {
        this.plugin = plugin;
    }

    public static PlatformHandler<Audience> of(Plugin plugin) {
        return new SpigotPlatformHandler(plugin);
    }

    @Override
    public boolean hasPermission(Audience player, String permission) {
        return !(player instanceof Player) || ((Player) player).hasPermission(permission);
    }

    @Override
    public boolean isOP(Audience player) {
        return player instanceof Player && ((Player) player).isOp();
    }

    @Override
    public void broadcastMessage(Collection<String> message, Placeholder... placeholders) {
        for (String s : message) {
            for (String handlePlaceholder : PlaceholderFactory.handlePlaceholders(s, placeholders)) {
                Bukkit.broadcast(MiniMessage.miniMessage().deserialize(handlePlaceholder));
            }
        }
    }

    @Override
    public void sendMessage(Audience player, Collection<String> message, Placeholder... placeholders) {
        for (String s : message) {
            for (String handlePlaceholder : PlaceholderFactory.handlePlaceholders(s, placeholders)) {
                player.sendMessage(MiniMessage.miniMessage().deserialize(handlePlaceholder));
            }
        }
    }

    @Override
    public boolean isServerThread() {
        return Bukkit.getServer().isPrimaryThread();
    }

    @Override
    public void runSync(Runnable runnable) {
        if (Bukkit.getServer().isPrimaryThread()) {
            runnable.run();
            return;
        }

        Bukkit.getScheduler().runTask(this.plugin, runnable);
    }

    @Override
    public void runLater(Runnable runnable, int delayTicks) {
        Bukkit.getScheduler().runTaskLater(this.plugin, runnable, delayTicks);
    }

    @Override
    public double getTPS() {
        return Bukkit.getServer().getTPS()[0];
    }

    @Override
    public void executeConsoleCommands(List<String> commands, Placeholder... placeholders) {
        for (String command : commands) {
            for (String handlePlaceholder : PlaceholderFactory.handlePlaceholders(command, placeholders)) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), handlePlaceholder);
            }
        }
    }

    @Override
    public void sendToast(Audience player, ConfigToast configToast) {
        //TODO:
    }

    @Override
    public void sendToast(EnvyPlayer<Audience> player, ConfigToast configToast) {
        //TODO:
    }

    @Override
    public boolean isItem(Object itemStack, ConfigItem item) {
        return false;
    }

    @Override
    public PlatformScanning scanning() {
        return NoOpPlatformScanning.INSTANCE; //TODO: implement scanning
    }
}
