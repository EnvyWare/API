package com.envyful.api.config.type.item.click.action;

import com.envyful.api.config.type.item.MenuItemContext;
import com.envyful.api.config.type.item.click.ClickAction;
import com.envyful.api.config.type.item.click.ClickActionId;
import com.envyful.api.gui.item.Displayable;
import com.envyful.api.player.EnvyPlayer;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
@ClickActionId("close")
public class CloseClickAction implements ClickAction {

    @Override
    public void handle(MenuItemContext context, Displayable.ClickType clickType) {
        context.player().closeInventory();
    }
}
