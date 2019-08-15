package fr.quatrevieux.araknemu.game.item.inventory;

import fr.quatrevieux.araknemu.game.item.inventory.event.KamasChanged;

/**
 * Store kamas
 */
public interface Wallet {
    /**
     * Get the current amount of kamas on the inventory
     *
     * @return The amount of kamas. Always positive
     */
    public long kamas();

    /**
     * Add kamas to the inventory
     * Will dispatch event {@link KamasChanged}
     *
     * @param quantity Quantity of kamas to add. Must be positive
     *
     * @throws IllegalArgumentException When a null or negative quantity is given
     */
    public void addKamas(long quantity);

    /**
     * Remove kamas from the inventory
     * Will dispatch event {@link KamasChanged}
     *
     * @param quantity Quantity of kamas to remove. Must be positive (and not zero)
     *
     * @throws IllegalArgumentException When an invalid quantity is given
     */
    public void removeKamas(long quantity);
}
