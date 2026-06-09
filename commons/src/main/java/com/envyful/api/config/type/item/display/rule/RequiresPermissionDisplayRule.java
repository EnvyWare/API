package com.envyful.api.config.type.item.display.rule;

import com.envyful.api.config.type.ConfigItem;
import com.envyful.api.config.type.item.MenuItemContext;
import com.envyful.api.config.type.item.ResolvedMenuItem;
import com.envyful.api.config.type.item.display.ItemDisplayRule;
import com.envyful.api.config.type.item.display.ItemDisplayRuleId;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
@ItemDisplayRuleId("requires_permission")
public class RequiresPermissionDisplayRule implements ItemDisplayRule {

    private String permission;
    private ConfigItem fallback;

    public RequiresPermissionDisplayRule() {}

    public RequiresPermissionDisplayRule(String permission, ConfigItem fallback) {
        this.permission = permission;
        this.fallback = fallback;
    }

    @Override
    public ResolvedMenuItem resolve(MenuItemContext context, ResolvedMenuItem currentItem) {
        var player = context.player();

        if (player == null) {
            return currentItem;
        }

        if (player.hasPermission(this.permission)) {
            return currentItem;
        }

        return currentItem.fallback(this.fallback);
    }
}
