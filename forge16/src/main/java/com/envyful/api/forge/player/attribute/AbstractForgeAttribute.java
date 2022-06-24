package com.envyful.api.forge.player.attribute;

import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.player.attribute.PlayerAttribute;

import java.util.UUID;

/**
 *
 * Abstract implementation of the {@link PlayerAttribute} for the forge platform. This handles storing the manager
 * and the forge implementation of the "parent" {@link ForgeEnvyPlayer} class.
 *
 * @param <A> The manager class parameter
 */
public abstract class AbstractForgeAttribute<A> implements PlayerAttribute<A> {

    protected final transient A manager;
    protected final transient ForgeEnvyPlayer parent;
    protected final UUID uuid;

    protected AbstractForgeAttribute(A manager, ForgeEnvyPlayer parent) {
        this.manager = manager;
        this.parent = parent;
        this.uuid = parent.getUuid();
    }

    protected AbstractForgeAttribute(UUID uuid) {
        this.manager = null;
        this.parent = null;
        this.uuid = uuid;
    }

    @Override
    public UUID getUuid() {
        return this.uuid;
    }

    public ForgeEnvyPlayer getParent() {
        return parent;
    }

    public A getManager() {
        return manager;
    }
}
