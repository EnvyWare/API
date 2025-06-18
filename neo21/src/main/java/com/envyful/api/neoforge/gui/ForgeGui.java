package com.envyful.api.neoforge.gui;

import com.envyful.api.gui.Gui;
import com.envyful.api.gui.item.Displayable;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.neoforge.gui.close.ForgeCloseConsumer;
import com.envyful.api.neoforge.gui.item.EmptySlot;
import com.envyful.api.neoforge.gui.pane.ForgeSimplePane;
import com.envyful.api.neoforge.player.ForgeEnvyPlayer;
import com.envyful.api.platform.PlatformProxy;
import com.envyful.api.player.EnvyPlayer;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * Forge implementation of the {@link Gui} interface.
 *
 */
public class ForgeGui implements Gui {

    private final Component title;
    private final int height;
    private final ForgeCloseConsumer closeConsumer;
    private final ForgeSimplePane parentPane;
    private final ForgeSimplePane[] panes;
    private final MenuType<?> containerType;

    private final List<ForgeGuiContainer> containers = new CopyOnWriteArrayList<>();

    ForgeGui(Component title, int height,
             ForgeCloseConsumer closeConsumer, Pane... panes) {
        this.title = title;
        this.height = height;
        this.closeConsumer = closeConsumer;
        this.parentPane = (ForgeSimplePane) new ForgeSimplePane.Builder().height(height).topLeftX(0).topLeftY(0).width(9).build();
        this.panes = new ForgeSimplePane[panes.length];
        int i = 0;

        for (Pane pane : panes) {
            if (!(pane instanceof ForgeSimplePane)) {
                continue;
            }

            this.panes[i] = (ForgeSimplePane) pane;
            ++i;
        }

        switch(height) {
            case 0: case 1: this.containerType = MenuType.GENERIC_9x1; break;
            case 2: this.containerType = MenuType.GENERIC_9x2; break;
            case 3: this.containerType = MenuType.GENERIC_9x3; break;
            case 4: this.containerType = MenuType.GENERIC_9x4; break;
            case 5: this.containerType = MenuType.GENERIC_9x5; break;
            default: case 6: this.containerType = MenuType.GENERIC_9x6; break;
        }
    }

    @Override
    public void open(EnvyPlayer<?> player) {
        if (!(player instanceof ForgeEnvyPlayer)) {
            return;
        }

        PlatformProxy.runSync(() -> {
            ServerPlayer parent = (ServerPlayer) player.getParent();
            parent.closeContainer();

            var provider = new SimpleMenuProvider((i, inventory, player1) -> new ForgeGuiContainer(this, parent), this.title);

            parent.openMenu(provider);
            ForgeGuiTracker.dequeueUpdate(parent);
            parent.containerMenu.broadcastChanges();
            this.containers.add(((ForgeGuiContainer) parent.containerMenu));
            ForgeGuiTracker.addGui(player, this);
        });
    }

    public void update() {
        for (ForgeGuiContainer value : this.containers) {
            boolean updated = false;
            for (ForgeSimplePane pane : value.gui.panes) {
                if (pane.getTickHandler() != null) {
                    updated = true;
                    pane.getTickHandler().tick(pane);
                }
            }

            if (updated) {
                value.update(this.panes, false);
            }
        }
    }

    public MenuType<?> getContainerType() {
        return this.containerType;
    }

    /**
     *
     * Forge container class for the GUI
     *
     */
    private final class ForgeGuiContainer extends AbstractContainerMenu {

        private ForgeGui gui;
        private final ServerPlayer player;
        private final List<EmptySlot> emptySlots = new ArrayList<>();
        private final NonNullList<ItemStack> inventoryItemStacks = NonNullList.create();

        private boolean closed = false;
        private boolean locked = false;
        private boolean updating = false;
        protected boolean suppressSlotUpdates = false;

        public ForgeGuiContainer(ForgeGui gui, ServerPlayer player) {
            super(gui.getContainerType(), 1);

            this.gui = gui;
            this.player = player;

            this.create(this.gui.panes);
        }

        @Override
        public void suppressRemoteUpdates() {
            super.suppressRemoteUpdates();

            this.suppressSlotUpdates = true;
        }

        @Override
        public void resumeRemoteUpdates() {
            super.resumeRemoteUpdates();

            this.suppressSlotUpdates = false;
        }

        @Override
        public Slot getSlot(int slotId) {
            if (this.slots.isEmpty()) {
                return null;
            }

            if (slotId >= this.slots.size()) {
                slotId = this.slots.size() - 1;
            } else if (slotId < 0) {
                slotId = 0;
            }

            return super.getSlot(slotId);
        }

        @Override
        public NonNullList<ItemStack> getItems() {
            NonNullList<ItemStack> nonnulllist = NonNullList.create();

            for(int i = 0; i < this.slots.size(); ++i) {
                nonnulllist.add(this.slots.get(i).getItem());
            }

            return nonnulllist;
        }

        public void create(ForgeSimplePane[] panes) {
            for (int i = 0; i < (9 * this.gui.height); i++) {
                var emptySlot = new EmptySlot(this.gui.parentPane, i);

                this.addSlot(emptySlot);

                this.emptySlots.add(emptySlot);
                this.inventoryItemStacks.add(ItemStack.EMPTY);
            }

            this.update(panes, false);
        }

        public void update(ForgeSimplePane[] panes, boolean force) {
            if (this.updating) {
                return;
            }

            this.updating = true;

            for (var pane : panes) {
                if (pane == null) {
                    continue;
                }

                for (int y = 0; y < pane.getItems().length; y++) {
                    var row = pane.getItems()[y];

                    for (int x = 0; x < row.length; x++) {
                        var item = row[x];
                        int index = pane.updateIndex((9 * y) + x);

                        if (index >= this.slots.size()) {
                            this.addSlot(item);
                        } else {
                            this.slots.set(index, item);
                        }

                        this.inventoryItemStacks.set(index, item.getItem());
                    }
                }
            }

            if (this.slots.size() <= (9 * this.gui.height)) {
                for (int i = 9; i < 36; i++) {
                    ItemStack itemStack = player.getInventory().items.get(i);
                    this.addSlot(new Slot(player.getInventory(), i, 0, 0), itemStack);
                }
                // Sets the slots for the hotbar.
                for (int i = 0; i < 9; i++) {
                    ItemStack itemStack = player.getInventory().items.get(i);
                    this.addSlot(new Slot(player.getInventory(), i, 0, 0), itemStack);
                }
            }

            if (force || ForgeGuiTracker.requiresUpdate(this.player)) {
                this.refreshPlayerContents();
            }

            this.updating = false;
        }

        private Slot addSlot(Slot slotIn, ItemStack itemStack) {
            super.addSlot(slotIn);
            this.inventoryItemStacks.add(itemStack);
            return slotIn;
        }

        @Override
        protected Slot addSlot(Slot slotIn) {
            super.addSlot(slotIn);
            this.inventoryItemStacks.add(ItemStack.EMPTY);
            return slotIn;
        }

        @Override
        public boolean canTakeItemForPickAll(ItemStack p_94530_1_, Slot p_94530_2_) {
            return false;
        }

        @Override
        public boolean stillValid(Player p_75145_1_) {
            return true;
        }

        @Override
        public ItemStack quickMoveStack(Player p_82846_1_, int p_82846_2_) {
            return ItemStack.EMPTY;
        }

        @Override
        protected boolean moveItemStackTo(ItemStack p_75135_1_, int p_75135_2_, int p_75135_3_, boolean p_75135_4_) {
            return false;
        }

        @Override
        public void setItem(int p_182407_, int p_182408_, ItemStack p_182409_) {

        }

        @Override
        public boolean canDragTo(Slot p_94531_1_) {
            return false;
        }

        @Override
        public void clicked(int slot, int dragType, ClickType clickTypeIn, Player player) {
            if (slot <= -1 || locked) {
                return;
            }

            this.clearPlayerCursor();

            Displayable.ClickType clickType = this.convertClickType(dragType, clickTypeIn);

            if (clickType == null) {
                return;
            }

            EnvyPlayer<?> envyPlayer = PlatformProxy.getPlayerManager().getPlayer(player.getUUID());

            if (envyPlayer == null) {
                return;
            }

            int xPos = slot % 9;
            int yPos = slot / 9;

            for (var pane : this.gui.panes) {
                if (!pane.inPane(xPos, yPos)) {
                    continue;
                }

                var panePosition = pane.convertXandY(xPos, yPos);

                ForgeSimplePane.SimpleDisplayableSlot simpleDisplayableSlot = pane.getItems()[panePosition.getY()][panePosition.getX()];
                simpleDisplayableSlot.getDisplayable().onClick(envyPlayer, clickType);
                ForgeGuiTracker.enqueueUpdate(envyPlayer);
            }
        }

        private Displayable.ClickType convertClickType(int id, ClickType clickType) {
            switch(id) {
                case 0 : return clickType == ClickType.QUICK_MOVE ? Displayable.ClickType.SHIFT_LEFT : Displayable.ClickType.LEFT;
                case 1 : return clickType == ClickType.QUICK_MOVE ? Displayable.ClickType.SHIFT_RIGHT : Displayable.ClickType.RIGHT;
                case 2 : return Displayable.ClickType.MIDDLE;
                default : return null;
            }
        }

        public void refreshPlayerContents() {
            if (this.suppressSlotUpdates) {
                return;
            }

            ForgeGuiTracker.dequeueUpdate(this.player);
            this.player.containerMenu.broadcastFullState();
        }

        private void clearPlayerCursor() {
            ClientboundContainerSetSlotPacket setCursorSlot = new ClientboundContainerSetSlotPacket(-1, 1, 0, ItemStack.EMPTY);
            player.connection.send(setCursorSlot);
        }

        @Override
        public void removed(Player playerIn) {
            if (this.closed) {
                return;
            }

            PlatformProxy.runSync(() -> this.handleClose(playerIn));
        }

        private void handleClose(Player playerIn) {
            this.closed = true;
            super.removed(player);

            var sender = (ServerPlayer) playerIn;
            var player = PlatformProxy.getPlayerManager().getPlayer(playerIn.getUUID());

            int windowId = sender.containerMenu.containerId;

//            ClientboundContainerClosePacket closeWindowServer = new ClientboundContainerClosePacket(windowId);
//            sender.connection.send(closeWindowServer);

            this.gui.closeConsumer.handle((ForgeEnvyPlayer) player);

            ForgeGui.this.containers.remove(this);
            ForgeGuiTracker.removePlayer(player);
        }
    }
}
