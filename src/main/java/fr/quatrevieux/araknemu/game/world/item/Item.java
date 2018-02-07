package fr.quatrevieux.araknemu.game.world.item;

import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.game.world.item.effect.ItemEffect;
import fr.quatrevieux.araknemu.game.world.item.effect.SpecialEffect;

import java.util.List;

/**
 * Base type for items
 */
public interface Item {
    /**
     * Get the template for the current item
     */
    public ItemTemplate template();

    /**
     * Get all item effects
     */
    public List<? extends ItemEffect> effects();

    /**
     * Get list of special effects
     */
    public List<SpecialEffect> specials();
}
