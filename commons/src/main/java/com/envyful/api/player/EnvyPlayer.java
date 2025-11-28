package com.envyful.api.player;

import com.envyful.api.config.ConfigLocation;
import com.envyful.api.platform.Messageable;
import com.envyful.api.platform.PlatformProxy;
import com.envyful.api.player.attribute.AttributeHolder;
import com.envyful.api.player.attribute.PlayerAttribute;
import com.envyful.api.text.Placeholder;
import com.envyful.api.text.parse.SimplePlaceholder;

/**
 *
 * This interface is designed to provide basic useful
 * methods for all the different player implementations independent
 * of the platform details (i.e. auto-translates
 * all text sent to the player, and makes it less complicated to do
 * different functions such as sending titles etc.).
 * <br>
 * It also stores {@link PlayerAttribute} from the
 * plugin implementation that will include specific data from the
 * plugin / mod. The attributes stored by the
 * plugin's / manager's class as to allow each mod / plugin to have multiple
 * attributes for storing different sets of data.
 *
 * @param <T> The specific platform implementation of the player object.
 */
public interface EnvyPlayer<T> extends SimplePlaceholder, Messageable<T>, AttributeHolder {

    /**
     *
     * Checks if the player is an operator
     *
     * @return If the player is an operator
     */
    default boolean isOP() {
        return PlatformProxy.isOP(this);
    }

    /**
     *
     * Sends an action bar message to the player
     *
     * @param message The message to send
     * @param placeholders The placeholders to replace in the message
     */
    void actionBar(String message, Placeholder... placeholders);

    /**
     *
     * Sends an action bar message to the player
     *
     * @param message The message to send
     */
    void actionBar(Object message);

    /**
     *
     * Execute the command as the player
     *
     * @param command The command to execute
     */
    void executeCommand(String command);

    /**
     *
     * Plays a sound to the player
     *
     * @param sound The sound to play
     * @param volume The volume of the sound
     * @param pitch The pitch of the sound
     */
    void playSound(String sound, float volume, float pitch);

    /**
     *
     * Plays a sound to the player
     *
     * @param sound The sound to play
     * @param volume The volume of the sound
     * @param pitch The pitch of the sound
     */
    void playSound(Object sound, float volume, float pitch);

    /**
     *
     * Execute the commands as the player
     *
     * @param commands The commands to execute
     */
    void executeCommands(String... commands);

    /**
     *
     * Closes the current inventory the player has open
     *
     */
    void closeInventory();

    /**
     *
     * Checks if the player has the given amount of empty slots
     * in their inventory
     *
     * @param emptySlots The amount of empty slots to check for
     * @return If the player has the given amount of empty slots
     */
    boolean hasInventorySpace(int emptySlots);

    /**
     *
     * Teleports the player to the given location
     *
     * @param location The location to teleport the player to
     */
    void teleport(ConfigLocation location);

    @Override
    default String replace(String line) {
        return line.replace("%player%", this.getName())
                .replace("%uuid%", this.getUniqueId().toString());
    }
}
