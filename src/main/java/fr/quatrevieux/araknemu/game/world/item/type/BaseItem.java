package fr.quatrevieux.araknemu.game.world.item.type;

import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.game.world.item.Item;
import fr.quatrevieux.araknemu.game.world.item.effect.ItemEffect;
import fr.quatrevieux.araknemu.game.world.item.effect.SpecialEffect;

import java.util.List;

/**
 * Base item class
 */
abstract public class BaseItem implements Item {
    final private ItemTemplate template;
    final private List<SpecialEffect> specials;

    public BaseItem(ItemTemplate template, List<SpecialEffect> specials) {
        this.template = template;
        this.specials = specials;
    }

    @Override
    public ItemTemplate template() {
        return template;
    }

    @Override
    public List<? extends ItemEffect> effects() {
        return specials;
    }

    @Override
    public List<SpecialEffect> specials() {
        return specials;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != getClass()) {
            return false;
        }

        BaseItem item = (BaseItem) obj;

        return template.equals(item.template)
            && specials.equals(item.specials)
        ;
    }

    @Override
    public int hashCode() {
        int result = template.hashCode();

        result = 31 * result + specials.hashCode();

        return result;
    }
}
