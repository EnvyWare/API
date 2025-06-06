package com.envyful.api.neoforge.concurrency;

import com.envyful.api.concurrency.UpdateBuilder;
import com.envyful.api.neoforge.listener.LazyListener;
import com.envyful.api.neoforge.player.util.UtilPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

/**
 *
 * Forge Implementation of the {@link UpdateBuilder} class
 *
 */
public class ForgeUpdateBuilder extends UpdateBuilder<ServerPlayer> {

    private static final BiPredicate<ServerPlayer, String> PERM_PREDICATE = UtilPlayer::hasPermission;
    private static final BiConsumer<ServerPlayer, String> MESSAGE_CONSUMER = (ServerPlayerEntity, s) -> ServerPlayerEntity.sendSystemMessage(Component.literal(s));

    private ForgeUpdateBuilder() {}

    @Override
    public void start() {
        super.start();

        new JoinListener();
    }

    private final class JoinListener extends LazyListener {

        @SubscribeEvent
        public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
            if (!ForgeUpdateBuilder.this.upToDate) {
                ForgeUpdateBuilder.this.attemptSendMessage((ServerPlayer) event.getEntity(), PERM_PREDICATE, MESSAGE_CONSUMER);
            }
        }
    }
}
