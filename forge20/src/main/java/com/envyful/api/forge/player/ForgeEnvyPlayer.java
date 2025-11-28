package com.envyful.api.forge.player;

import com.envyful.api.concurrency.UtilLogger;
import com.envyful.api.config.ConfigLocation;
import com.envyful.api.forge.world.UtilWorld;
import com.envyful.api.platform.PlatformProxy;
import com.envyful.api.player.AbstractEnvyPlayer;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.text.Placeholder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.List;
import java.util.UUID;

/**
 *
 * Forge implementation of the {@link EnvyPlayer} interface
 *
 */
public class ForgeEnvyPlayer extends AbstractEnvyPlayer<ServerPlayer> {

    protected ForgeEnvyPlayer(ServerPlayer player) {
        super();

        this.parent = player;
    }

    @Override
    public UUID getUniqueId() {
        return this.parent.getUUID();
    }

    @Override
    public String getName() {
        return this.parent.getName().getString();
    }

    @Override
    public void message(Object... messages) {
        for (Object message : messages) {
            if (message instanceof String) {
                this.getParent().sendSystemMessage(PlatformProxy.parse((String) message));
            } else if (message instanceof Component) {
                this.getParent().sendSystemMessage((Component) message);
            } else if (message instanceof List) {
                for (Object subMessage : ((List) message)) {
                    this.message(subMessage);
                }
            } else {
                throw new RuntimeException("Unsupported message type");
            }
        }
    }

    @Override
    public void actionBar(String message, Placeholder... placeholders) {
        this.actionBar(PlatformProxy.parse(message, placeholders).get(0));
    }

    @Override
    public void actionBar(Object message) {
        if (message instanceof String string) {
            this.actionBar(string, new Placeholder[0]);
        } else if (message instanceof Component component) {
            this.getParent().sendSystemMessage(component, true);
        } else {
            throw new RuntimeException("Unsupported message type");
        }
    }

    @Override
    public void playSound(String sound, float volume, float pitch) {
        this.playSound(BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.tryParse(sound)), volume, pitch);
    }

    @Override
    public void playSound(Object sound, float volume, float pitch) {
        if (sound instanceof String) {
            this.playSound((String) sound, volume, pitch);
            return;
        }

        if (!(sound instanceof SoundEvent)) {
            throw new RuntimeException("Unsupported sound type");
        }

        this.parent.playNotifySound((SoundEvent) sound, SoundSource.MASTER, volume, pitch);
    }

    @Override
    public void executeCommands(String... commands) {
        for (String command : commands) {
            this.executeCommand(command);
        }
    }

    @Override
    public void executeCommand(String command) {
        ServerLifecycleHooks.getCurrentServer().getCommands().performPrefixedCommand(this.parent.createCommandSourceStack(), command);
    }

    @Override
    public void teleport(ConfigLocation location) {
        PlatformProxy.runSync(() -> {
            var world = (ServerLevel) UtilWorld.findWorld(location.getWorldName());

            if (world == null) {
                UtilLogger.getLogger().error("Failed to find world " + location.getWorldName() + " when teleporting " + this.getName() + " possible worlds are: " + String.join(", ", UtilWorld.getWorldNames()));
                return;
            }

            this.getParent().teleportTo(world,
                    location.getPosX(), location.getPosY(), location.getPosZ(), (float) location.getPitch(), (float) location.getYaw());
        });
    }

    @Override
    public void closeInventory() {
        this.getParent().closeContainer();
    }

    @Override
    public boolean hasInventorySpace(int emptySlots) {
        int spaces = 0;

        for (int i = 0; i < this.getParent().getInventory().items.size(); i++) {
            if (this.getParent().getInventory().getItem(i).isEmpty()) {
                spaces++;
            }
        }

        return spaces >= emptySlots;
    }
}
