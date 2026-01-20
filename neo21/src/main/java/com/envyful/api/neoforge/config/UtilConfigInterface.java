package com.envyful.api.neoforge.config;

import com.envyful.api.config.type.ConfigItem;
import com.envyful.api.config.type.PaginatedConfigInterface;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.gui.item.Displayable;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.math.TriConsumer;
import com.envyful.api.neoforge.gui.close.ForgeCloseConsumer;
import com.envyful.api.neoforge.player.ForgeEnvyPlayer;
import com.envyful.api.platform.PlatformProxy;
import com.envyful.api.text.Placeholder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.ObjIntConsumer;

public class UtilConfigInterface {

    public static <T> PaginatedBuilder<T> paginatedBuilder(List<T> items) {
        return new PaginatedBuilder<T>().items(items);
    }

    public static class PaginatedBuilder<T> {

        private PaginatedConfigInterface configInterface;
        private List<T> items = new ArrayList<>();
        private Function<T, ConfigItem> itemConfigItemConversion;
        private Function<T, Displayable> itemDisplayableConversion;
        private ForgeCloseConsumer closeConsumer = (ForgeCloseConsumer) GuiFactory.closeConsumerBuilder().build();
        private TriConsumer<ForgeEnvyPlayer, Displayable.ClickType, T> pageItemClickHandler = (forgeEnvyPlayer, clickType, t) -> {};
        private List<ObjIntConsumer<Pane>> extraItems = new ArrayList<>();

        private PaginatedBuilder() {
            // Private constructor for static factory method
        }

        public PaginatedBuilder<T> configSettings(PaginatedConfigInterface configInterface) {
            this.configInterface = configInterface;
            return this;
        }

        public PaginatedBuilder<T> items(Collection<? extends T> items) {
            this.items.addAll(items);
            return this;
        }

        public PaginatedBuilder<T> items(T... items) {
            this.items.addAll(Arrays.asList(items));
            return this;
        }

        public PaginatedBuilder<T> itemDisplayableConversion(Function<T, ConfigItem> itemDisplayableConversion) {
            this.itemConfigItemConversion = itemDisplayableConversion;
            return this;
        }

        public PaginatedBuilder<T> itemConversion(Function<T, Displayable> itemDisplayableConversion) {
            this.itemDisplayableConversion = itemDisplayableConversion;
            return this;
        }

        public PaginatedBuilder<T> closeConsumer(ForgeCloseConsumer closeConsumer) {
            this.closeConsumer = closeConsumer;
            return this;
        }

        public PaginatedBuilder<T> pageItemClickHandler(TriConsumer<ForgeEnvyPlayer, Displayable.ClickType, T> pageItemClickHandler) {
            this.pageItemClickHandler = pageItemClickHandler;
            return this;
        }

        public PaginatedBuilder<T> extraItems(Collection<ObjIntConsumer<Pane>> extraItems) {
            this.extraItems.addAll(extraItems);
            return this;
        }

        public PaginatedBuilder<T> extraItems(ObjIntConsumer<Pane>... extraItems) {
            this.extraItems.addAll(Arrays.asList(extraItems));
            return this;
        }

        public void open(ForgeEnvyPlayer player, Placeholder... placeholders) {
            open(player, 1, placeholders);
        }

        public void open(ForgeEnvyPlayer player, int page, Placeholder... placeholders) {
            int pages = this.items.size() / this.configInterface.getPositions().size();
            var finalPlaceholders = Arrays.copyOfRange(placeholders, 0, placeholders.length + 4);
            finalPlaceholders[placeholders.length] = Placeholder.simple("%current_page%", String.valueOf(page));
            finalPlaceholders[placeholders.length + 1] = Placeholder.simple("%next_page%", String.valueOf(page == pages ? (this.configInterface.isLoopPages() ? 1 : pages) : page + 1));
            finalPlaceholders[placeholders.length + 2] = Placeholder.simple("%previous_page%", String.valueOf(page == 1 ? (this.configInterface.isLoopPages() ? pages : 1) : page - 1));
            finalPlaceholders[placeholders.length + 3] = Placeholder.simple("%total_pages%", String.valueOf(pages));

            var pane = this.configInterface.toPane(finalPlaceholders);


            if (this.configInterface.isDisplayPageButtonsAtLimits() || (pages > page)) {
                this.configInterface.getNextPageButton()
                        .convertToBuilder(player, pane, finalPlaceholders)
                        .clickHandler((envyPlayer, clickType) -> {
                            int nextPage = page + 1;

                            if (nextPage > (pages + 1) && this.configInterface.isLoopPages()) {
                                nextPage = 1;
                            }

                            open(player, nextPage, placeholders);
                        })
                        .build();
            }

            if (this.configInterface.isDisplayPageButtonsAtLimits() || (page > 1)) {
                this.configInterface.getPreviousPageButton()
                        .convertToBuilder(player, pane, finalPlaceholders)
                        .clickHandler((envyPlayer, clickType) -> {
                            int previousPage = page - 1;

                            if (previousPage < 1 && this.configInterface.isLoopPages()) {
                                previousPage = pages;
                            }

                            open(player, previousPage, placeholders);
                        })
                        .build();
            }

            for (int i = 0; i < this.configInterface.getPositions().size(); i++) {
                int itemId = ((page - 1) * this.configInterface.getPositions().size()) + i;

                if (itemId >= (this.items.size()) || itemId < 0) {
                    continue;
                }

                int position = this.configInterface.getPositions().get(i);
                int posX = position % 9;
                int posY = position / 9;
                T item = this.items.get(itemId);

                pane.set(posX, posY, this.getDisplayable(player, item, finalPlaceholders));
            }

            for (var extraItem : this.extraItems) {
                extraItem.accept(pane, page);
            }

            GuiFactory.guiBuilder()
                    .addPane(pane)
                    .height(this.configInterface.getHeight())
                    .closeConsumer(this.closeConsumer)
                    .title(PlatformProxy.flatParse(this.configInterface.getTitle(), finalPlaceholders))
                    .build()
                    .open(player);
        }

        private Displayable getDisplayable(ForgeEnvyPlayer player, T item, Placeholder... placeholders) {
            if (this.itemDisplayableConversion != null) {
                return this.itemDisplayableConversion.apply(item);
            }

            return GuiFactory.displayableBuilder(
                            UtilConfigItem.fromConfigItem(this.itemConfigItemConversion.apply(item), placeholders))
                    .singleClick()
                    .asyncClick()
                    .clickHandler((envyPlayer, clickType) -> this.pageItemClickHandler.execute(player, clickType, item))
                    .build();
        }
    }
}
