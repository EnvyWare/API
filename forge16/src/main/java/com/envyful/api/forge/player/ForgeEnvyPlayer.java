package com.envyful.api.forge.player;

import com.envyful.api.concurrency.UtilLogger;
import com.envyful.api.config.ConfigLocation;
import com.envyful.api.forge.world.UtilWorld;
import com.envyful.api.platform.PlatformProxy;
import com.envyful.api.player.AbstractEnvyPlayer;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.text.Placeholder;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.List;
import java.util.UUID;

/**
 *
 * Forge implementation of the {@link EnvyPlayer} interface
 *
 */
public class ForgeEnvyPlayer extends AbstractEnvyPlayer<ServerPlayerEntity> {

    protected ForgeEnvyPlayer(ServerPlayerEntity player) {
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
                this.getParent().sendMessage(PlatformProxy.parse((String) message), Util.NIL_UUID);
            } else if (message instanceof ITextComponent) {
                this.getParent().sendMessage((ITextComponent) message, Util.NIL_UUID);
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
        if (message instanceof String) {
            this.actionBar((String) message, new Placeholder[0]);
        } else if (message instanceof ITextComponent) {
            this.getParent().sendMessage((ITextComponent) message, ChatType.GAME_INFO, Util.NIL_UUID);
        } else {
            throw new RuntimeException("Unsupported message type");
        }
    }

    @Override
    public void playSound(String sound, float volume, float pitch) {
        this.playSound(Registry.SOUND_EVENT.get(ResourceLocation.tryParse(sound)), volume, pitch);
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

        this.parent.playNotifySound((SoundEvent) sound, SoundCategory.MASTER, volume, pitch);
    }

    @Override
    public void executeCommands(String... commands) {
        for (String command : commands) {
            this.executeCommand(command);
        }
    }

    @Override
    public void executeCommand(String command) {
        ServerLifecycleHooks.getCurrentServer().getCommands().performCommand(this.parent.createCommandSourceStack(), command);
    }

    @Override
    public void teleport(ConfigLocation location) {
        PlatformProxy.runSync(() -> {
            var world = (ServerWorld) UtilWorld.findWorld(location.getWorldName());

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

        for (int i = 0; i < this.getParent().inventory.items.size(); i++) {
            if (this.getParent().inventory.getItem(i).isEmpty()) {
                spaces++;
            }
        }

        return spaces >= emptySlots;
    }
}
