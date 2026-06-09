package com.envyful.api.config.type.item.display;

import com.envyful.api.config.type.item.MenuItemContext;
import com.envyful.api.config.type.item.ResolvedMenuItem;

public interface ItemDisplayRule {

    ResolvedMenuItem resolve(MenuItemContext context, ResolvedMenuItem currentItem);

}
