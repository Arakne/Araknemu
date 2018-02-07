package fr.quatrevieux.araknemu.game.item.factory;

import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.game.world.item.Item;
import fr.quatrevieux.araknemu.game.world.item.Type;

/**
 * Factory for items
 */
public interface ItemFactory {
    /**
     * Create the item
     *
     * @param template The item template
     * @param maximize Maximize stats ?
     */
    public Item create(ItemTemplate template, boolean maximize);

    /**
     * List of supported types
     */
    public Type[] types();
}
