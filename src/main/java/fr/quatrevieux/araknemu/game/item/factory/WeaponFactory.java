package fr.quatrevieux.araknemu.game.item.factory;

import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.game.world.item.Item;
import fr.quatrevieux.araknemu.game.world.item.Type;
import fr.quatrevieux.araknemu.game.world.item.effect.mapping.EffectToCharacteristicMapping;
import fr.quatrevieux.araknemu.game.world.item.effect.mapping.EffectToSpecialMapping;
import fr.quatrevieux.araknemu.game.world.item.effect.mapping.EffectToWeaponMapping;
import fr.quatrevieux.araknemu.game.world.item.type.Weapon;

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
    public Item create(ItemTemplate template, boolean maximize) {
        return new Weapon(
            template,
            template
                .effects()
                .stream()
                .filter(entry -> entry.effect().type() == Effect.Type.WEAPON)
                .map(weaponMapping::create)
                .collect(Collectors.toList()),
            template
                .effects()
                .stream()
                .filter(entry -> entry.effect().type() == Effect.Type.CHARACTERISTIC)
                .map(
                    maximize
                        ? characteristicMapping::createMaximize
                        : characteristicMapping::createRandom
                )
                .collect(Collectors.toList()),
            template
                .effects()
                .stream()
                .filter(entry -> entry.effect().type() == Effect.Type.SPECIAL)
                .map(specialMapping::create)
                .collect(Collectors.toList())
        );
    }

    @Override
    public Type[] types() {
        return new Type[] {
            Type.ARC, Type.BAGUETTE, Type.BAGUETTE, Type.DAGUES, Type.EPEE, Type.MARTEAU,
            Type.PELLE, Type.HACHE, Type.OUTIL, Type.PIOCHE, Type.FAUX, Type.ARBALETE,
            Type.ARME_MAGIQUE
        };
    }
}
