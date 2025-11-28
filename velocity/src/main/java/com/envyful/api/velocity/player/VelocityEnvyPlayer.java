package com.envyful.api.velocity.player;

import com.envyful.api.config.ConfigLocation;
import com.envyful.api.player.AbstractEnvyPlayer;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.text.Placeholder;
import com.envyful.api.text.PlaceholderFactory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.UUID;

/**
 *
 * Velocity implementation of the {@link EnvyPlayer} interface
 *
 */
public class VelocityEnvyPlayer extends AbstractEnvyPlayer<Player> {

    private final ProxyServer proxy;
    private final UUID uuid;

    protected VelocityEnvyPlayer(ProxyServer proxy, UUID uuid) {
        super();

        this.proxy = proxy;
        this.uuid = uuid;
    }

    @Override
    public UUID getUniqueId() {
        return this.uuid;
    }

    @Override
    public String getName() {
        return this.parent.getUsername();
    }

    @Override
    public void message(Object... messages) {
        for (Object message : messages) {
            if (message instanceof Component) {
                this.getParent().sendMessage((Component) message);
            } else if (message instanceof String) {
                //TODO: convert
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
        if (message instanceof Component) {
            this.getParent().sendActionBar((Component) message);
        } else if (message instanceof String) {
            this.actionBar((String) message, new Placeholder[0]);
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
        this.proxy.getCommandManager().executeAsync(this.getParent(), command);
    }

    @Override
    public void teleport(ConfigLocation location) {
        throw new UnsupportedOperationException("Cannot teleport players on the proxy!");
    }

    @Override
    public void closeInventory() {
        //TODO:
    }

    @Override
    public boolean hasInventorySpace(int emptySlots) {
        return false;
    }
}
