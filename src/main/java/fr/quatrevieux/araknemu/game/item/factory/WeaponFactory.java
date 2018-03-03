package fr.quatrevieux.araknemu.game.item.factory;

import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.game.item.GameItemSet;
import fr.quatrevieux.araknemu.game.world.item.Item;
import fr.quatrevieux.araknemu.game.world.item.Type;
import fr.quatrevieux.araknemu.game.item.effect.mapping.EffectToCharacteristicMapping;
import fr.quatrevieux.araknemu.game.item.effect.mapping.EffectToSpecialMapping;
import fr.quatrevieux.araknemu.game.item.effect.mapping.EffectToWeaponMapping;
import fr.quatrevieux.araknemu.game.item.type.Weapon;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Factory for weapons
 */
final public class WeaponFactory implements ItemFactory {
    final private EffectToWeaponMapping weaponMapping;
    final private EffectToCharacteristicMapping characteristicMapping;
    final private EffectToSpecialMapping specialMapping;

    public WeaponFactory(EffectToWeaponMapping weaponMapping, EffectToCharacteristicMapping characteristicMapping, EffectToSpecialMapping specialMapping) {
        this.weaponMapping = weaponMapping;
        this.characteristicMapping = characteristicMapping;
        this.specialMapping = specialMapping;
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
            effects
                .stream()
                .filter(entry -> entry.effect().type() == Effect.Type.WEAPON)
                .map(weaponMapping::create)
                .collect(Collectors.toList()),
            effects
                .stream()
                .filter(entry -> entry.effect().type() == Effect.Type.CHARACTERISTIC)
                .map(
                    maximize
                        ? characteristicMapping::createMaximize
                        : characteristicMapping::createRandom
                )
                .collect(Collectors.toList()),
            effects
                .stream()
                .filter(entry -> entry.effect().type() == Effect.Type.SPECIAL)
                .map(entry -> specialMapping.create(entry, maximize))
                .collect(Collectors.toList())
        );
    }
}
