package fr.quatrevieux.araknemu.game.item.factory;

import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.game.item.GameItemSet;
import fr.quatrevieux.araknemu.game.item.effect.CharacteristicEffect;
import fr.quatrevieux.araknemu.game.item.effect.SpecialEffect;
import fr.quatrevieux.araknemu.game.item.effect.WeaponEffect;
import fr.quatrevieux.araknemu.game.item.effect.mapping.EffectMappers;
import fr.quatrevieux.araknemu.game.item.type.Weapon;
import fr.quatrevieux.araknemu.game.world.item.Item;
import fr.quatrevieux.araknemu.game.world.item.Type;

import java.util.List;

/**
 * Factory for weapons
 */
final public class WeaponFactory implements ItemFactory {
    final private EffectMappers mappers;

    public WeaponFactory(EffectMappers mappers) {
        this.mappers = mappers;
    }

    @Override
    public Item create(ItemTemplate template, GameItemSet set, boolean maximize) {
        return create(template, set, template.effects(), maximize);
    }

    @Override
    public Item retrieve(ItemTemplate template, GameItemSet set, List<ItemTemplateEffectEntry> effects) {
        return create(template, set, effects, false);
    }

    @Override
    public Type[] types() {
        return new Type[] {
            Type.ARC, Type.BAGUETTE, Type.BATON, Type.DAGUES, Type.EPEE, Type.MARTEAU,
            Type.PELLE, Type.HACHE, Type.OUTIL, Type.PIOCHE, Type.FAUX, Type.ARBALETE,
            Type.ARME_MAGIQUE
        };
    }

    private Item create(ItemTemplate template, GameItemSet set, List<ItemTemplateEffectEntry> effects, boolean maximize) {
        return new Weapon(
            template,
            set,
            mappers.get(WeaponEffect.class).create(effects),
            mappers.get(CharacteristicEffect.class).create(effects, maximize),
            mappers.get(SpecialEffect.class).create(effects, maximize)
        );
    }
}
