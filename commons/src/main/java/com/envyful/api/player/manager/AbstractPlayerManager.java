package com.envyful.api.player.manager;

import com.envyful.api.player.Attribute;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.player.PlayerManager;
import com.envyful.api.player.attribute.manager.PlatformAgnosticAttributeManager;
import com.envyful.api.player.name.NameStore;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;

public abstract class AbstractPlayerManager<A extends EnvyPlayer<B>, B> extends PlatformAgnosticAttributeManager<A> implements PlayerManager<A, B> {

    protected final Map<UUID, A> cachedPlayers = new ConcurrentHashMap<>();
    protected final Function<B, UUID> uuidGetter;
    protected NameStore nameStore = null;

    protected AbstractPlayerManager(BiConsumer<UUID, Throwable> errorHandler, Function<B, UUID> uuidGetter) {
        super(errorHandler);

        this.uuidGetter = uuidGetter;
    }

    protected AbstractPlayerManager(Function<B, UUID> uuidGetter) {
        super();

        this.uuidGetter = uuidGetter;
    }

    @Override
    public A getPlayer(B player) {
        if (player == null) {
            return null;
        }

        return this.getPlayer(this.uuidGetter.apply(player));
    }

    @Override
    public A getPlayer(UUID uuid) {
        if (uuid == null) {
            return null;
        }

        return this.cachedPlayers.get(uuid);
    }

    @Override
    public A getOnlinePlayer(String username) {
        for (A online : this.cachedPlayers.values()) {
            if (online.getName().equals(username)) {
                return online;
            }
        }

        return null;
    }

    @Override
    public A getOnlinePlayerCaseInsensitive(String username) {
        for (A online : this.cachedPlayers.values()) {
            if (online.getName().equalsIgnoreCase(username)) {
                return online;
            }
        }

        return null;
    }

    @Override
    public List<A> getOnlinePlayers() {
        return List.copyOf(this.cachedPlayers.values());
    }

    @Nullable
    @Override
    public NameStore getNameStore() {
        return this.nameStore;
    }

    @Override
    public void setNameStore(NameStore nameStore) {
        this.nameStore = nameStore;
    }

    @Override
    public <T extends Attribute> UUID mapId(Class<T> attributeClass, UUID uuid) {
        var player = this.getPlayer(uuid);

        if (player != null && player.hasAttribute(attributeClass)) {
            var attribute = player.getAttributeNow(attributeClass);

            return attribute.getUniqueId();
        }

        return super.mapId(attributeClass, uuid);
    }
}
