package com.envyful.api.command;

import com.envyful.api.command.tab.TabHandler;
import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.api.concurrency.UtilLogger;
import com.envyful.api.text.UtilString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;

/**
 *
 * Represents a command that can be executed on any given platform
 *
 * @param <C> The sender type
 */
public abstract class PlatformCommand<C> {

    protected final String name;
    protected final BiFunction<C, List<String>, List<String>> descriptionProvider;
    protected final BiPredicate<C, List<String>> permissionCheck;
    protected final Function<C, List<String>> noPermissionProvider;
    protected final List<String> aliases;
    protected final PlatformCommandExecutor<C> executor;
    protected final List<PlatformCommand<C>> subCommands;
    protected final TabHandler<C> tabHandler;
    protected final boolean includePlayersWithArguments;

    protected PlatformCommand(Builder<C> builder) {
        this.name = builder.name;
        this.descriptionProvider = builder.descriptionProvider;
        this.permissionCheck = builder.permissionCheck;
        this.noPermissionProvider = builder.noPermissionProvider;
        this.aliases = builder.aliases;
        this.executor = builder.executor;
        this.subCommands = builder.subCommands;
        this.tabHandler = builder.tabHandler;
        this.includePlayersWithArguments = builder.includePlayersWithArguments;
    }

    public String getName() {
        return this.name;
    }

    public List<String> getAliases() {
        return this.aliases;
    }

    /**
     *
     * Checks that the sender has access to use this command
     *
     * @param sender The sender
     * @return True if they have access
     */
    public boolean checkPermission(C sender, List<String> args) {
        return this.permissionCheck == null || this.permissionCheck.test(sender, args);
    }

    public void execute(C sender, String[] args) throws IllegalArgumentException {
        if (sender == null) {
            UtilLogger.getLogger().error("No sender provided for command {}", this.name);
            return;
        }

        if (args == null) {
            UtilLogger.getLogger().error("No args provided for command {}", this.name);
            return;
        }

        UtilConcurrency.runAsync(() -> this.executeSync(sender, args));
    }

    protected void executeSync(C sender, String[] args) {
        if (!this.checkPermission(sender, List.of(args))) {
            this.sendNoPermission(sender);
            return;
        }

        if (args.length > 0) {
            for (PlatformCommand<C> subCommand : this.subCommands) {
                if (this.fitsCommand(args[0], subCommand)) {
                    subCommand.executeSync(sender, Arrays.copyOfRange(args, 1, args.length));
                    return;
                }
            }
        }

        if (this.executor != null) {
            this.executor.execute(sender, args);
            return;
        }

        if (this.descriptionProvider != null) {
            List<String> apply = this.descriptionProvider.apply(sender, List.of(args));
            this.sendSystemMessage(sender, apply);
        }
    }

    protected void sendNoPermission(C sender) {
        if (this.noPermissionProvider == null) {
            return;
        }

        this.sendSystemMessage(sender, this.noPermissionProvider.apply(sender));
    }

    protected abstract void sendSystemMessage(C sender, List<String> message);

    protected boolean fitsCommand(String arg, PlatformCommand<C> subCommand) {
        if (subCommand.name.equalsIgnoreCase(arg)) {
            return true;
        }

        for (String alias : subCommand.aliases) {
            if (alias.equalsIgnoreCase(arg)) {
                return true;
            }
        }

        return false;
    }

    public CompletableFuture<List<String>> getTabCompletions(C sender, String[] args) {
        if (sender == null) {
            UtilLogger.getLogger().error("No sender provided for command {}", this.name);
            return CompletableFuture.completedFuture(new ArrayList<>());
        }

        if (args == null) {
            UtilLogger.getLogger().error("No args provided for command {}", this.name);
            return CompletableFuture.completedFuture(new ArrayList<>());
        }

        return CompletableFuture.supplyAsync(ArrayList::new, UtilConcurrency.SCHEDULED_EXECUTOR_SERVICE)
                .thenCompose(ignore -> {
                    if (args.length > 0) {
                        for (PlatformCommand<C> subCommand : this.subCommands) {
                            if (this.fitsCommand(args[0], subCommand)) {
                                return subCommand.getTabCompletions(sender, Arrays.copyOfRange(args, 1, args.length));
                            }
                        }
                    }

                    if (this.tabHandler != null) {
                        return this.tabHandler.getCompletions(sender, args);
                    }

                    return CompletableFuture.completedFuture(new ArrayList<>());
                }).thenApply(tabCompletions -> {
                    if (tabCompletions.isEmpty()) {
                        tabCompletions.addAll(this.getAccessibleSubCommands(sender, List.of(args)));
                    }

                    if (tabCompletions.isEmpty()) {
                        if (args.length == 0) {
                            tabCompletions.addAll(this.getOnlinePlayerNames());
                        } else {
                            tabCompletions.addAll(this.getPlayers(args[0]));
                        }
                    }

                    return tabCompletions;
                });
    }

    protected List<String> getPlayers(String name) {
        if (name.isEmpty()) {
            return this.getOnlinePlayerNames();
        }

        return UtilString.getMatching(name, this.getOnlinePlayerNames());
    }

    protected abstract List<String> getOnlinePlayerNames();

    protected List<String> getAccessibleSubCommands(C sender, List<String> args) {
        List<String> subCommands = new ArrayList<>();

        for (PlatformCommand<C> subCommand : this.subCommands) {
            if (subCommand.checkPermission(sender, args)) {
                subCommands.addAll(subCommand.aliases);
            }
        }

        return subCommands;
    }

    /**
     *
     * Builder for the PlatformCommand
     *
     * @param <C> The sender type
     */
    public static abstract class Builder<C> {

        protected String name;
        protected BiFunction<C, List<String>, List<String>> descriptionProvider;
        protected BiPredicate<C, List<String>> permissionCheck;
        protected Function<C, List<String>> noPermissionProvider;
        protected List<String> aliases = new ArrayList<>();
        protected PlatformCommandExecutor<C> executor;
        protected TabHandler<C> tabHandler;
        protected List<PlatformCommand<C>> subCommands = new ArrayList<>();
        protected boolean includePlayersWithArguments;

        public Builder<C> name(String name) {
            this.name = name;
            return this;
        }

        public Builder<C> descriptionProvider(BiFunction<C, List<String>, List<String>> descriptionProvider) {
            this.descriptionProvider = descriptionProvider;
            return this;
        }

        public Builder<C> permissionCheck(BiPredicate<C, List<String>> permissionCheck) {
            this.permissionCheck = permissionCheck;
            return this;
        }

        public Builder<C> noPermissionProvider(Function<C, List<String>> noPermissionProvider) {
            this.noPermissionProvider = noPermissionProvider;
            return this;
        }

        public Builder<C> aliases(List<String> aliases) {
            this.aliases.addAll(aliases);
            return this;
        }

        public Builder<C> executor(PlatformCommandExecutor<C> executor) {
            this.executor = executor;
            return this;
        }

        public Builder<C> tabHandler(TabHandler<C> tabHandler) {
            this.tabHandler = tabHandler;
            return this;
        }

        public Builder<C> subCommands(List<PlatformCommand<C>> subCommands) {
            this.subCommands.addAll(subCommands);
            return this;
        }

        protected PlatformCommand<C> build(Function<Builder<C>, ? extends PlatformCommand<C>> constructor) {
            return constructor.apply(this);
        }

        public abstract PlatformCommand<C> build();
    }
}
