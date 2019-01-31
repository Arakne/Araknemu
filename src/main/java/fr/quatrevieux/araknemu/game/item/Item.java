package fr.quatrevieux.araknemu.game.item;

import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemType;
import fr.quatrevieux.araknemu.game.item.effect.ItemEffect;
import fr.quatrevieux.araknemu.game.item.effect.SpecialEffect;

import java.util.List;
import java.util.Optional;

/**
 * Base type for items
 */
public interface Item {
    /**
     * Get the template for the current item
     */
    public ItemTemplate template();

    /**
     * Get the related item set
     */
    public Optional<GameItemSet> set();

    /**
     * Get all item effects
     */
    public List<? extends ItemEffect> effects();

    /**
     * Get list of special effects
     */
    public List<SpecialEffect> specials();

    /**
     * Get the item type
     */
    public ItemType type();
}
