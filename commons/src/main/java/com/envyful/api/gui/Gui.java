package com.envyful.api.gui;

import com.envyful.api.gui.close.CloseConsumer;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.player.EnvyPlayer;

import java.util.concurrent.CompletableFuture;

/**
 *
 * An interface representing chest GUIs for the platform specific implementation
 *
 */
public interface Gui {

    /**
     *
     * Opens the GUI for the given player
     *
     * @param player The player to open the GUI for
     */
    CompletableFuture<Void> open(EnvyPlayer<?> player);

    /**
     *
     * Gui builder interface
     *
     */
    interface Builder {

        /**
         *
         * Sets the title of the GUI
         *
         * @param title The title of the GUI
         * @return The builder
         */
        Builder title(Object title);

        /**
         *
         * Sets the height of the GUI
         *
         * @param height The height of the GUI
         * @return The builder
         */
        Builder height(int height);

        /**
         *
         * Adds a {@link Pane} to the GUI
         *
         * @param pane The pane to add
         * @return The builder
         */
        Builder addPane(Pane pane);

        /**
         *
         * Sets the close consumer instance
         *
         * @param closeConsumer The close consumer handler
         * @return The builder
         */
        Builder closeConsumer(CloseConsumer<?, ?> closeConsumer);

        /**
         *
         * Builds the GUI from the given specifications
         *
         * @return The new GUI
         */
        Gui build();

    }
}
