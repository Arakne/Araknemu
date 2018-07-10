package fr.quatrevieux.araknemu.game.fight.castable.weapon;

import fr.quatrevieux.araknemu.game.fight.castable.Castable;
import fr.quatrevieux.araknemu.game.item.type.Weapon;
import fr.quatrevieux.araknemu.game.spell.SpellConstraints;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Adapt weapon item to castable
 */
final public class CastableWeapon implements Castable {
    final private Weapon weapon;

    private List<SpellEffect> effects;
    private List<SpellEffect> criticalEffects;

    public CastableWeapon(Weapon weapon) {
        this.weapon = weapon;
    }

    @Override
    public List<SpellEffect> effects() {
        if (effects == null) {
            effects = weapon.weaponEffects().stream()
                .map(effect -> new CastableWeaponEffect(effect, weapon))
                .collect(Collectors.toList())
            ;
        }

        return effects;
    }

    @Override
    public List<SpellEffect> criticalEffects() {
        if (criticalEffects == null) {
            criticalEffects = weapon.weaponEffects().stream()
                .map(effect -> new CastableWeaponEffect(effect, weapon, true))
                .collect(Collectors.toList())
            ;
        }

        return criticalEffects;
    }

    @Override
    public int apCost() {
        return weapon.info().apCost();
    }

    @Override
    public int criticalHit() {
        return weapon.info().criticalRate();
    }

    @Override
    public int criticalFailure() {
        return weapon.info().failureRate();
    }

    @Override
    public SpellConstraints constraints() {
        return new WeaponConstraints(weapon);
    }
}
