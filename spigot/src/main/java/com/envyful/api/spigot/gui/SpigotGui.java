package com.envyful.api.spigot.gui;

import com.envyful.api.gui.Gui;
import com.envyful.api.gui.item.Displayable;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.platform.PlatformProxy;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.spigot.gui.close.SpigotCloseConsumer;
import com.envyful.api.spigot.gui.item.SpigotSimpleDisplayable;
import com.envyful.api.spigot.gui.pane.SpigotSimplePane;
import com.envyful.api.spigot.player.SpigotEnvyPlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import java.util.concurrent.CompletableFuture;

/**
 *
 * Spigot implementation of the {@link Gui} interface.
 *
 */
public class SpigotGui implements Gui {

    private final Component title;
    private final int height;
    private final SpigotCloseConsumer closeConsumer;
    private final SpigotSimplePane parentPane;
    private final SpigotSimplePane[] panes;

    SpigotGui(Component title, int height, SpigotCloseConsumer closeConsumer,
              Pane... panes) {
        this.title = title;
        this.height = height;
        this.closeConsumer = closeConsumer;
        this.parentPane = (SpigotSimplePane) new SpigotSimplePane.Builder().height(height).topLeftX(0).topLeftY(0).width(9).build();
        this.panes = new SpigotSimplePane[panes.length];
        int i = 0;

        for (Pane pane : panes) {
            if (!(pane instanceof SpigotSimplePane)) {
                continue;
            }

            this.panes[i] = (SpigotSimplePane) pane;
            ++i;
        }
    }

    @Override
    public CompletableFuture<Void> open(EnvyPlayer<?> player) {
        if (!(player instanceof SpigotEnvyPlayer)) {
            return null;
        }

        var future = new CompletableFuture<Void>();
        Player parent = (Player)player.getParent();
        SpigotGuiTracker.InventoryDetails details = SpigotGuiTracker.getDetails(player);
        Inventory inventory = details != null ? details.getInventory() : Bukkit.createInventory(null, this.height * 9, this.title);

        inventory.clear();

        for (SpigotSimplePane pane : panes) {
            if (pane == null) {
                continue;
            }

            for (int y = 0; y < pane.getItems().length; y++) {
                Displayable[] row = pane.getItems()[y];

                if (row == null) {
                    continue;
                }

                for (int x = 0; x < row.length; x++) {
                    Displayable item = row[x];

                    if (item == null) {
                        continue;
                    }

                    int index = pane.updateIndex((9 * y) + x);

                    inventory.setItem(index, SpigotSimpleDisplayable.Converter.toNative(item));
                }
            }
        }

        if (details == null) {
            parent.openInventory(inventory);
        } else {
            details.getGui().closeConsumer.handle((SpigotEnvyPlayer) player);
        }

        SpigotGuiTracker.addGui(player, this, inventory);
        future.complete(null);
        return future;
    }

    public static class Listener implements org.bukkit.event.Listener  {

        @EventHandler
        public void onInventoryClick(InventoryClickEvent event) {
            if (!(event.getWhoClicked() instanceof Player)) {
                return;
            }

            SpigotGuiTracker.InventoryDetails details = SpigotGuiTracker.getDetails((Player) event.getWhoClicked());

            if (details == null) {
                return;
            }

            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            Displayable.ClickType clickType = this.convert(event.getClick());

            for (SpigotSimplePane pane : details.getGui().panes) {
                if (pane == null) {
                    continue;
                }

                for (int y = 0; y < pane.getItems().length; y++) {
                    Displayable[] row = pane.getItems()[y];

                    if (row == null) {
                        continue;
                    }

                    for (int x = 0; x < row.length; x++) {
                        Displayable item = row[x];

                        if (item == null) {
                            continue;
                        }

                        int index = pane.updateIndex((9 * y) + x);

                        if (index != event.getSlot()) {
                            continue;
                        }

                        item.onClick(PlatformProxy.getPlayerManager().getPlayer(player.getUniqueId()), clickType);
                    }
                }
            }
        }

        private Displayable.ClickType convert(ClickType clickType) {
            try {
                return Displayable.ClickType.valueOf(clickType.name());
            } catch (Exception e) {
                return Displayable.ClickType.RIGHT;
            }
        }

        @EventHandler
        public void onInventoryClose(InventoryCloseEvent event) {
            if (!(event.getPlayer() instanceof Player)) {
                return;
            }

            SpigotGuiTracker.InventoryDetails details = SpigotGuiTracker.getDetails((Player) event.getPlayer());

            if (details == null) {
                return;
            }

            var player = PlatformProxy.getPlayerManager().getPlayer(event.getPlayer().getUniqueId());

            details.getGui().closeConsumer.handle((SpigotEnvyPlayer) player);
            SpigotGuiTracker.removePlayer(player);
        }
    }
}
