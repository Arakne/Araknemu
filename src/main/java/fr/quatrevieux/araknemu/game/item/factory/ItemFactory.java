package fr.quatrevieux.araknemu.game.item.factory;

import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemType;
import fr.quatrevieux.araknemu.game.item.GameItemSet;
import fr.quatrevieux.araknemu.game.item.Item;
import fr.quatrevieux.araknemu.game.item.SuperType;

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
    public Item create(ItemTemplate template, ItemType type, GameItemSet set, boolean maximize);

    /**
     * Retrieve an item
     *
     * @param template The item template
     * @param effects The item effects
     */
    public Item retrieve(ItemTemplate template, ItemType type, GameItemSet set, List<ItemTemplateEffectEntry> effects);

    /**
     * Get the supported item super type
     */
    public SuperType type();
}
