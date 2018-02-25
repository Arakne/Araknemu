package fr.quatrevieux.araknemu.game.item.type;

import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.game.item.effect.SpecialEffect;

import java.util.List;

/**
 * Simple resource item
 */
final public class Resource extends BaseItem {
    public Resource(ItemTemplate template, List<SpecialEffect> specials) {
        super(template, specials);
    }
}
