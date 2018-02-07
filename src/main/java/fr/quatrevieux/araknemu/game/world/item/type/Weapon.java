package fr.quatrevieux.araknemu.game.world.item.type;

import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.game.world.item.effect.CharacteristicEffect;
import fr.quatrevieux.araknemu.game.world.item.effect.ItemEffect;
import fr.quatrevieux.araknemu.game.world.item.effect.SpecialEffect;
import fr.quatrevieux.araknemu.game.world.item.effect.WeaponEffect;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for weapons
 */
final public class Weapon extends Equipment {
    final private List<WeaponEffect> weaponEffects;

    public Weapon(ItemTemplate template, List<WeaponEffect> weaponEffects, List<CharacteristicEffect> characteristics, List<SpecialEffect> specials) {
        super(template, characteristics, specials);

        this.weaponEffects = weaponEffects;
    }

    @Override
    public List<? extends ItemEffect> effects() {
        List<ItemEffect> effects = new ArrayList<>(weaponEffects);

        effects.addAll(super.effects());

        return effects;
    }

    public List<WeaponEffect> weaponEffects() {
        return weaponEffects;
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }

        Weapon weapon = (Weapon) obj;

        return weaponEffects.equals(weapon.weaponEffects);
    }
}
