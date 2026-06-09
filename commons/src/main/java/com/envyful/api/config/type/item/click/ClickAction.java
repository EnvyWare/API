package com.envyful.api.config.type.item.click;

import com.envyful.api.config.type.item.MenuItemContext;
import com.envyful.api.config.type.item.click.action.CloseClickAction;
import com.envyful.api.gui.item.Displayable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@FunctionalInterface
@ConfigSerializable
public interface ClickAction {

    default void apply(Displayable.Builder<?> builder) {

    }

    void handle(MenuItemContext context, Displayable.ClickType clickType);

    static ClickAction close() {
        return new CloseClickAction();
    }
}
