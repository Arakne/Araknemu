package fr.quatrevieux.araknemu.game.item.type;

import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemType;
import fr.quatrevieux.araknemu.game.item.GameItemSet;
import fr.quatrevieux.araknemu.game.item.Item;
import fr.quatrevieux.araknemu.game.item.effect.ItemEffect;
import fr.quatrevieux.araknemu.game.item.effect.SpecialEffect;

import java.util.List;
import java.util.Optional;

/**
 * Base item class
 */
abstract public class BaseItem implements Item {
    final private ItemTemplate template;
    final private ItemType type;
    final private GameItemSet set;
    final private List<SpecialEffect> specials;

    public BaseItem(ItemTemplate template, ItemType type, GameItemSet set, List<SpecialEffect> specials) {
        this.template = template;
        this.type = type;
        this.set = set;
        this.specials = specials;
    }

    @Override
    public ItemTemplate template() {
        return template;
    }

    @Override
    public Optional<GameItemSet> set() {
        return Optional.ofNullable(set);
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
    public ItemType type() {
        return type;
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
