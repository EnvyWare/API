package com.envyful.api.config.type.item.click.action;

import com.envyful.api.config.type.item.MenuItemContext;
import com.envyful.api.config.type.item.click.ClickAction;
import com.envyful.api.config.type.item.click.ClickActionId;
import com.envyful.api.gui.item.Displayable;
import com.envyful.api.platform.PlatformProxy;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;

@ConfigSerializable
@ClickActionId("execute_console_commands")
public class ExecuteConsoleCommandsClickAction implements ClickAction {

    private List<String> commands;

    public ExecuteConsoleCommandsClickAction() {
    }

    public ExecuteConsoleCommandsClickAction(List<String> commands) {
        this.commands = commands;
    }

    public ExecuteConsoleCommandsClickAction(String... commands) {
        this.commands = List.of(commands);
    }

    @Override
    public void handle(MenuItemContext context, Displayable.ClickType clickType) {
        PlatformProxy.executeConsoleCommands(this.commands, context);
    }
}
