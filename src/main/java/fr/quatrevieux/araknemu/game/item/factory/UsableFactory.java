package fr.quatrevieux.araknemu.game.item.factory;

import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.game.item.GameItemSet;
import fr.quatrevieux.araknemu.game.world.item.Item;
import fr.quatrevieux.araknemu.game.world.item.Type;
import fr.quatrevieux.araknemu.game.item.effect.mapping.EffectToSpecialMapping;
import fr.quatrevieux.araknemu.game.item.effect.mapping.EffectToUseMapping;
import fr.quatrevieux.araknemu.game.item.type.UsableItem;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Factory for usable items.
 */
final public class UsableFactory implements ItemFactory {
    final private EffectToUseMapping useMapping;
    final private EffectToSpecialMapping specialMapping;

    public UsableFactory(EffectToUseMapping useMapping, EffectToSpecialMapping specialMapping) {
        this.useMapping = useMapping;
        this.specialMapping = specialMapping;
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
            effects
                .stream()
                .filter(entry -> entry.effect().type() == Effect.Type.USE)
                .map(useMapping::create)
                .collect(Collectors.toList()),
            effects
                .stream()
                .filter(entry -> entry.effect().type() == Effect.Type.SPECIAL)
                .map(entry -> specialMapping.create(entry, maximize))
                .collect(Collectors.toList())
        );
    }
}
