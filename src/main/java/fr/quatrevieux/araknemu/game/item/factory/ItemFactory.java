package fr.quatrevieux.araknemu.game.item.factory;

import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.game.item.GameItemSet;
import fr.quatrevieux.araknemu.game.world.item.Item;
import fr.quatrevieux.araknemu.game.world.item.Type;

import java.util.List;

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
    public Item create(ItemTemplate template, GameItemSet set, boolean maximize);

    /**
     * Retrieve an item
     *
     * @param template The item template
     * @param effects The item effects
     */
    public Item retrieve(ItemTemplate template, GameItemSet set, List<ItemTemplateEffectEntry> effects);

    /**
     * List of supported types
     */
    public Type[] types();
}
