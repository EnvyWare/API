package com.envyful.api.spigot.player;

import com.envyful.api.config.ConfigLocation;
import com.envyful.api.platform.PlatformProxy;
import com.envyful.api.player.AbstractEnvyPlayer;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.text.Placeholder;
import com.envyful.api.text.PlaceholderFactory;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 *
 * Spigot implementation of the {@link EnvyPlayer} interface
 *
 */
public class SpigotEnvyPlayer extends AbstractEnvyPlayer<Player> {

    protected final UUID uuid;

    protected SpigotEnvyPlayer(UUID uuid) {
        super();

        this.uuid = uuid;
    }

    @Override
    public UUID getUniqueId() {
        return this.uuid;
    }

    @Override
    public String getName() {
        return this.parent.getName();
    }

    @Override
    public void message(Object... messages) {
        for (Object message : messages) {
            if (message instanceof Component) {
                this.getParent().sendMessage((Component) message);
            } else if (message instanceof String text) {
                this.getParent().sendMessage(PlatformProxy.<Component>parse(text));
            }
        }
    }

    @Override
    public void actionBar(String message, Placeholder... placeholders) {
        var converted = PlaceholderFactory.handlePlaceholders(message, placeholders);

        if (converted.isEmpty()) {
            return;
        }

        this.getParent().sendActionBar(MiniMessage.miniMessage().deserialize(converted.get(0)));
    }

    @Override
    public void actionBar(Object message) {
        if (message instanceof Component component) {
            this.getParent().sendActionBar(component);
        } else if (message instanceof String string) {
            this.actionBar(string, new Placeholder[0]);
        } else {
            throw new RuntimeException("Unsupported message type");
        }
    }

    @Override
    public void playSound(String sound, float volume, float pitch) {
        this.playSound(Sound.sound(Key.key(sound), Sound.Source.MASTER, volume, pitch), volume, pitch);
    }

    @Override
    public void playSound(Object sound, float volume, float pitch) {
        if (sound instanceof String) {
            this.playSound((String) sound, volume, pitch);
            return;
        }

        if (!(sound instanceof Sound)) {
            throw new RuntimeException("Unsupported sound type");
        }

        this.parent.playSound((Sound) sound);
    }

    @Override
    public void executeCommands(String... commands) {
        for (String command : commands) {
            this.executeCommand(command);
        }
    }

    @Override
    public void executeCommand(String command) {
        this.getParent().performCommand(command);
    }

    @Override
    public void teleport(ConfigLocation location) {
        //TODO:
    }

    @Override
    public void closeInventory() {
        this.getParent().closeInventory();
    }

    @Override
    public boolean hasInventorySpace(int emptySlots) {
        int spaces = 0;

        for (int i = 0; i < this.getParent().getInventory().getSize(); i++) {
            var item = this.getParent().getInventory().getItem(i);
            if (item == null || item.isEmpty()) {
                spaces++;
            }
        }

        return spaces >= emptySlots;
    }
}
