package com.envyful.api.forge.gui.close;

import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.gui.close.CloseConsumer;
import com.envyful.api.platform.PlatformProxy;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 *
 * Forge based implementation of a {@link CloseConsumer}
 *
 */
public class ForgeCloseConsumer implements CloseConsumer<ForgeEnvyPlayer, ServerPlayer> {

    private final int delayTicks;
    private final Predicate<ForgeEnvyPlayer> predicate;
    private final Consumer<ForgeEnvyPlayer> handler;
    private final boolean async;

    public ForgeCloseConsumer(int delayTicks, Predicate<ForgeEnvyPlayer> predicate, Consumer<ForgeEnvyPlayer> handler, boolean async) {
        this.delayTicks = delayTicks;
        this.predicate = predicate;
        this.handler = handler;
        this.async = async;
    }

    @Override
    public void handle(ForgeEnvyPlayer player) {
        if (!this.predicate.test(player)) {
            return;
        }

        if (this.delayTicks <= 0) {
            if (this.async) {
                UtilConcurrency.runAsync(() -> this.handler.accept(player));
            } else {
                PlatformProxy.runSync(() -> this.handler.accept(player));
            }
            return;
        }

        if (this.async) {
            UtilConcurrency.runLater(() -> this.handler.accept(player), this.delayTicks * 50L);
        } else {
            PlatformProxy.runLater(() -> this.handler.accept(player), this.delayTicks);
        }
    }

    public static class Builder implements CloseConsumer.Builder<ForgeEnvyPlayer, ServerPlayer> {

        private int delayTicks = 0;
        private Predicate<ForgeEnvyPlayer> predicate = forgeEnvyPlayer -> true;
        private Consumer<ForgeEnvyPlayer> handler = forgeEnvyPlayer -> {};
        private boolean async = true;

        public Builder() {}

        @Override
        public CloseConsumer.Builder<ForgeEnvyPlayer, ServerPlayer> delayTicks(int delayTicks) {
            this.delayTicks = delayTicks;
            return this;
        }

        @Override
        public CloseConsumer.Builder<ForgeEnvyPlayer, ServerPlayer> predicate(Predicate<ForgeEnvyPlayer> predicate) {
            this.predicate = predicate;
            return this;
        }

        @Override
        public CloseConsumer.Builder<ForgeEnvyPlayer, ServerPlayer> handler(Consumer<ForgeEnvyPlayer> player) {
            this.handler = player;
            return this;
        }

        @Override
        public CloseConsumer.Builder<ForgeEnvyPlayer, ServerPlayer> async(boolean async) {
            this.async = async;
            return this;
        }

        @Override
        public CloseConsumer<ForgeEnvyPlayer, ServerPlayer> build() {
            return new ForgeCloseConsumer(this.delayTicks, this.predicate, this.handler, this.async);
        }
    }
}
