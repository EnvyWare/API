package com.envyful.api.neoforge.command.command;

import com.envyful.api.command.PlatformCommand;
import com.envyful.api.command.PlatformCommandExecutor;
import com.envyful.api.command.tab.TabHandler;
import com.envyful.api.platform.PlatformProxy;
import net.minecraft.commands.CommandSource;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;

public class ForgePlatformCommand extends PlatformCommand<CommandSource> {

    protected ForgePlatformCommand(Builder builder) {
        super(builder);
    }

    @Override
    protected void sendSystemMessage(CommandSource sender, List<String> message) {
        List<Component> components = PlatformProxy.parse(message);

        for (var encodedMessage : components) {
            sender.sendSystemMessage(encodedMessage);
        }
    }

    @Override
    protected List<String> getOnlinePlayerNames() {
        var playerNames = ServerLifecycleHooks.getCurrentServer().getPlayerNames();
        List<String> names = new ArrayList<>();

        names.addAll(Arrays.asList(playerNames));

        return names;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends PlatformCommand.Builder<CommandSource> {

        private Builder() {
            super();
        }

        @Override
        public Builder name(String name) {
            return (Builder) super.name(name);
        }

        @Override
        public Builder descriptionProvider(BiFunction<CommandSource, List<String>, List<String>> descriptionProvider) {
            return (Builder) super.descriptionProvider(descriptionProvider);
        }

        @Override
        public Builder permissionCheck(BiPredicate<CommandSource, List<String>> permissionCheck) {
            return (Builder) super.permissionCheck(permissionCheck);
        }

        @Override
        public Builder noPermissionProvider(Function<CommandSource, List<String>> noPermissionProvider) {
            return (Builder) super.noPermissionProvider(noPermissionProvider);
        }

        @Override
        public Builder aliases(List<String> aliases) {
            return (Builder) super.aliases(aliases);
        }

        @Override
        public Builder executor(PlatformCommandExecutor executor) {
            return (Builder) super.executor(executor);
        }

        @Override
        public Builder tabHandler(TabHandler<CommandSource> tabHandler) {
            return (Builder) super.tabHandler(tabHandler);
        }

        @Override
        public Builder subCommands(List<PlatformCommand<CommandSource>> subCommands) {
            return (Builder) super.subCommands(subCommands);
        }

        @Override
        public PlatformCommand<CommandSource> build() {
            return this.build(builder -> new ForgePlatformCommand((Builder) builder));
        }
    }
}
