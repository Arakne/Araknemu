package fr.quatrevieux.araknemu.game.world.item.inventory;

import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.game.world.item.Item;

import java.util.List;

/**
 * Entry for inventory
 */
public interface ItemEntry {
    final static public int DEFAULT_POSITION = -1;

    /**
     * Get the entry id
     * This value is unique over the inventory
     */
    public int id();

    /**
     * Get the entry position
     *
     * @see ItemEntry#isDefaultPosition() To check if the entry is on default position
     */
    public int position();

    /**
     * Check if the entry is on the default position
     *
     * @see ItemEntry#position() For get the real position
     */
    default public boolean isDefaultPosition() {
        return position() == DEFAULT_POSITION;
    }

    /**
     * Get the related item
     */
    public Item item();

    /**
     * Get the item quantity
     */
    public int quantity();

    /**
     * Add the quantity to the entry
     */
    public void add(int quantity);

    /**
     * Remove the quantity to the entry
     */
    public void remove(int quantity);

    /**
     * Export the item effects
     */
    public List<ItemTemplateEffectEntry> effects();

    /**
     * Get the template id
     */
    public int templateId();
}
