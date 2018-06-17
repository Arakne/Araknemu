package fr.quatrevieux.araknemu.game.item.type;

import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemType;
import fr.quatrevieux.araknemu.game.item.GameItemSet;
import fr.quatrevieux.araknemu.game.item.Item;
import fr.quatrevieux.araknemu.game.item.effect.ItemEffect;
import fr.quatrevieux.araknemu.game.item.effect.SpecialEffect;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Simple resource item
 */
final public class Resource implements Item {
    final private ItemTemplate template;
    final private ItemType type;
    final private List<SpecialEffect> specials;

    public Resource(ItemTemplate template, ItemType type, List<SpecialEffect> specials) {
        this.template = template;
        this.type = type;
        this.specials = specials;
    }

    @Override
    public ItemTemplate template() {
        return template;
    }

    @Override
    public Optional<GameItemSet> set() {
        return Optional.empty();
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

        Resource item = (Resource) obj;

        return template.equals(item.template)
            && specials.equals(item.specials)
        ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(template, specials);
    }
}
