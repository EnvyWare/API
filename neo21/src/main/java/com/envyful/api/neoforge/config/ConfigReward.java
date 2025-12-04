package com.envyful.api.neoforge.config;

import com.envyful.api.config.type.ExtendedConfigItem;
import com.envyful.api.platform.PlatformProxy;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.text.Placeholder;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ConfigSerializable
public class ConfigReward {

    protected String displayName = "Example Display Name";
    protected ExtendedConfigItem displayItem;
    protected List<String> commands;
    protected List<String> messages;
    protected int requiredInventorySpace = -1;

    protected ConfigReward(Builder builder) {
        this.displayName = builder.displayName;
        this.displayItem = builder.displayItem;
        this.commands = builder.commands;
        this.messages = builder.messages;
        this.requiredInventorySpace = builder.requiredInventorySpace;
    }

    public ConfigReward() {
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public ExtendedConfigItem getDisplayItem() {
        return this.displayItem;
    }

    public List<String> getCommands() {
        return this.commands;
    }

    public List<String> getMessages() {
        return this.messages;
    }

    public boolean hasSufficientInventorySpace(EnvyPlayer<?> player) {
        return this.requiredInventorySpace == -1 || player.hasInventorySpace(this.requiredInventorySpace);
    }

    public void execute(Placeholder... placeholders) {
        this.execute(null, placeholders);
    }

    public void execute(@Nullable ServerPlayer player, Placeholder... placeholders) {
        if (player != null) {
            placeholders = Arrays.copyOf(placeholders, placeholders.length + 1);
            placeholders[placeholders.length - 1] = Placeholder.simple("%player%", player.getName().getString());
        }

        if (this.commands != null && !this.commands.isEmpty()) {
            PlatformProxy.executeConsoleCommands(this.commands, placeholders);
        }

        if (player != null && this.messages != null && !this.messages.isEmpty()) {
            PlatformProxy.sendMessage(player, this.messages, placeholders);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        protected String displayName = "Example Display Name";
        protected ExtendedConfigItem displayItem;
        protected List<String> commands = new ArrayList<>();
        protected List<String> messages = new ArrayList<>();
        protected int requiredInventorySpace = -1;

        protected Builder() {}

        public Builder displayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public Builder displayItem(ExtendedConfigItem displayItem) {
            this.displayItem = displayItem;
            return this;
        }

        public Builder commands(String... commands) {
            return this.commands(List.of(commands));
        }

        public Builder commands(List<String> commands) {
            this.commands.addAll(commands);
            return this;
        }

        public Builder messages(String... messages) {
            return this.messages(List.of(messages));
        }

        public Builder messages(List<String> messages) {
            this.messages.addAll(messages);
            return this;
        }

        public Builder requiredInventorySpace(int requiredInventorySpace) {
            this.requiredInventorySpace = requiredInventorySpace;
            return this;
        }

        public ConfigReward build() {
            return new ConfigReward(this);
        }
    }
}
