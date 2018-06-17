package fr.quatrevieux.araknemu.game.item.factory;

import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemType;
import fr.quatrevieux.araknemu.game.item.GameItemSet;
import fr.quatrevieux.araknemu.game.item.Item;
import fr.quatrevieux.araknemu.game.item.SuperType;
import fr.quatrevieux.araknemu.game.item.effect.SpecialEffect;
import fr.quatrevieux.araknemu.game.item.effect.mapping.EffectMappers;
import fr.quatrevieux.araknemu.game.item.type.Resource;

import java.util.List;

/**
 * Factory for resources.
 * Will be used as default factory
 */
final public class ResourceFactory implements ItemFactory {
    final private EffectMappers mappers;

    public ResourceFactory(EffectMappers mappers) {
        this.mappers = mappers;
    }

    @Override
    public Item create(ItemTemplate template, ItemType type, GameItemSet set, boolean maximize) {
        return create(template, type, template.effects(), maximize);
    }

    @Override
    public Item retrieve(ItemTemplate template, ItemType type, GameItemSet set, List<ItemTemplateEffectEntry> effects) {
        return create(template, type, effects, false);
    }

    @Override
    public SuperType type() {
        return SuperType.RESOURCE;
    }

    private Item create(ItemTemplate template, ItemType type, List<ItemTemplateEffectEntry> effects, boolean maximize) {
        return new Resource(
            template,
            type,
            mappers.get(SpecialEffect.class).create(effects, maximize)
        );
    }
}
