package com.envyful.api.config.type.item;

import com.envyful.api.InitializationTask;
import com.envyful.api.config.ConfigTypeSerializer;
import com.envyful.api.config.type.item.click.ClickAction;
import com.envyful.api.config.type.item.click.ClickActionId;
import com.envyful.api.config.type.item.display.ItemDisplayRule;
import com.envyful.api.config.type.item.display.ItemDisplayRuleId;
import com.envyful.api.registry.Registry;

public class ConfigMenuItemRegistries implements InitializationTask {

    private static final Registry<String, Class<ItemDisplayRule>> DISPLAY_RULES = Registry.scannedClassRegistry(ItemDisplayRule.class, ItemDisplayRuleId.class);
    private static final Registry<String, Class<ClickAction>> CLICK_ACTIONS = Registry.scannedClassRegistry(ClickAction.class, ClickActionId.class);

    @Override
    public void run() {
        ConfigTypeSerializer.register(DISPLAY_RULES.getTypeSerializer(), ItemDisplayRule.class);
        ConfigTypeSerializer.register(CLICK_ACTIONS.getTypeSerializer(), ClickAction.class);
    }
}
