package fr.quatrevieux.araknemu.game.item.factory;

import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemType;
import fr.quatrevieux.araknemu.game.item.GameItemSet;
import fr.quatrevieux.araknemu.game.item.Item;
import fr.quatrevieux.araknemu.game.item.SuperType;
import fr.quatrevieux.araknemu.game.item.effect.SpecialEffect;
import fr.quatrevieux.araknemu.game.item.effect.UseEffect;
import fr.quatrevieux.araknemu.game.item.effect.mapping.EffectMappers;
import fr.quatrevieux.araknemu.game.item.type.UsableItem;

import java.util.List;

/**
 * Factory for usable items.
 */
final public class UsableFactory implements ItemFactory {
    final private EffectMappers mappers;

    public UsableFactory(EffectMappers mappers) {
        this.mappers = mappers;
    }

    @Override
    public Item create(ItemTemplate template, ItemType type, GameItemSet set, boolean maximize) {
        return create(template, type, template.effects());
    }

    @Override
    public Item retrieve(ItemTemplate template, ItemType type, GameItemSet set, List<ItemTemplateEffectEntry> effects) {
        return create(template, type, effects);
    }

    @Override
    public SuperType type() {
        return SuperType.USABLE;
    }

    private Item create(ItemTemplate template, ItemType type, List<ItemTemplateEffectEntry> effects) {
        return new UsableItem(
            template,
            type,
            mappers.get(UseEffect.class).create(effects),
            mappers.get(SpecialEffect.class).create(effects, true)
        );
    }
}
