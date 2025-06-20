package com.envyful.api.gui.item;

import com.envyful.api.player.EnvyPlayer;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 *
 * An interface that represents a displayable item in a page / container that can be clicked, and periodically updated.
 *
 */
public interface Displayable {

    /**
     *
     * Called when the displayable entity has been clicked by a player
     *
     * @param player The player that clicked the object
     * @param clickType The type of click that occurred
     */
    void onClick(EnvyPlayer<?> player, ClickType clickType);

    /**
     *
     * An enum representing the type of click coming from the user
     *
     */
    enum ClickType {

        LEFT,
        SHIFT_LEFT,
        MIDDLE,
        SHIFT_RIGHT,
        RIGHT,

        ;

    }

    /**
     *
     * An interface to build the platform's implementation of the Displayable interface
     *
     * @param <T> The platform's ItemStack class
     */
    interface Builder<T> {

        /**
         *
         * Sets the itemstack for the displayable
         *
         * @param itemStack The item to be displayed
         * @return The builder
         */
        Builder<T> itemStack(T itemStack);

        /**
         *
         * Gets the click handler set in the builder
         *
         * @return The click handler
         */
        BiConsumer<EnvyPlayer<?>, Displayable.ClickType> clickHandler();

        /**
         *
         * Sets the click handler for the displayable
         *
         * @param clickHandler The consumer for when the displayable is clicked
         * @return The builder
         */
        Builder<T> clickHandler(BiConsumer<EnvyPlayer<?>, ClickType> clickHandler);

        /**
         *
         * Sets the click to be handled asynchronously
         *
         * @return The builder
         */
        default Builder<T> asyncClick() {
            return this.asyncClick(true);
        }

        /**
         *
         * Sets the click to be handled synchronously
         *
         * @return The builder
         */
        default Builder<T> syncClick() {
            return this.asyncClick(false);
        }

        /**
         *
         * The delay between clicking and the function executing
         *
         * @param tickDelay The delay in ticks
         * @return The builder
         */
        Builder<T> delayTicks(int tickDelay);

        /**
         *
         * Sets the click to be handled asynchronously
         *
         * @return The builder
         */
        Builder<T> asyncClick(boolean async);

        /**
         *
         * Sets the button so it can only be clicked once
         *
         * @return The builder
         */
        default Builder<T> singleClick() {
            return this.singleClick(true);
        }

        /**
         *
         * Sets if the button can only be clicked once
         *
         * @param singleClick True if only once
         * @return The builder
         */
        Builder<T> singleClick(boolean singleClick);

        /**
         *
         * Allowed delay between user's clicks (defaults to 50 millis i.e. 1 tick)
         *
         * @param milliseconds The delay in milliseconds
         * @return The builder
         */
        Builder<T> clickDelay(long milliseconds);

        /**
         *
         * The number of clicks to lock the user out after (defaults to 100)
         *
         * @param clickLockCount The click count to lock the user out after
         * @return The builder
         */
        Builder<T> lockOutClicks(int clickLockCount);

        /**
         *
         * Creates the displayable from the specifications
         *
         * @return The new displayable implementation
         */
        default Displayable build() {
            return this.build(item -> {});
        }


        /**
         *
         * Creates the displayable from the specifications and applies the item consumer
         *
         * @param itemConsumer The consumer to apply to the item
         * @return The new displayable implementation
         */
        default Displayable build(Consumer<T> itemConsumer) {
            return this.build();
        }
    }
}
