package fr.quatrevieux.araknemu.game.item.factory;

import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.game.item.GameItemSet;
import fr.quatrevieux.araknemu.game.item.effect.SpecialEffect;
import fr.quatrevieux.araknemu.game.item.effect.UseEffect;
import fr.quatrevieux.araknemu.game.item.effect.mapping.EffectMappers;
import fr.quatrevieux.araknemu.game.item.type.UsableItem;
import fr.quatrevieux.araknemu.game.world.item.Item;
import fr.quatrevieux.araknemu.game.world.item.Type;

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
    public Item create(ItemTemplate template, GameItemSet set, boolean maximize) {
        return create(template, template.effects(), maximize);
    }

    @Override
    public Item retrieve(ItemTemplate template, GameItemSet set, List<ItemTemplateEffectEntry> effects) {
        return create(template, effects, false);
    }

    @Override
    public Type[] types() {
        return new Type[] {
            Type.BIERE,
            Type.BOISSON,
            Type.DOCUMENT,
            Type.DONS,
            Type.BOOST_FOOD,
            Type.CADEAUX,
            Type.COMESTI_POISSON,
            Type.FEE_ARTIFICE,
            Type.PAIN,
            Type.PARCHEMIN_CARAC,
            Type.PARCHEMIN_SORT,
            Type.PARCHO_EXP,
            Type.PARCHO_RECHERCHE,
            Type.POTION,
            Type.POTION_FAMILIER,
            Type.POTION_METIER,
            Type.POTION_OUBLIE,
            Type.POTION_SORT,
            Type.PRISME,
            Type.VIANDE_COMESTIBLE
        };
    }

    private Item create(ItemTemplate template, List<ItemTemplateEffectEntry> effects, boolean maximize) {
        return new UsableItem(
            template,
            mappers.get(UseEffect.class).create(effects),
            mappers.get(SpecialEffect.class).create(effects, true)
        );
    }
}
