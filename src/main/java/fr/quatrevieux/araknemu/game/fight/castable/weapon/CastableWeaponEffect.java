package fr.quatrevieux.araknemu.game.fight.castable.weapon;

import fr.quatrevieux.araknemu.game.item.effect.WeaponEffect;
import fr.quatrevieux.araknemu.game.item.type.Weapon;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.game.spell.effect.area.SpellEffectArea;

/**
 * Effect for a weapon
 */
final public class CastableWeaponEffect implements SpellEffect {
    final private WeaponEffect effect;
    final private Weapon weapon;
    final private boolean critical;

    public CastableWeaponEffect(WeaponEffect effect, Weapon weapon) {
        this(effect, weapon, false);
    }

    public CastableWeaponEffect(WeaponEffect effect, Weapon weapon, boolean critical) {
        this.effect = effect;
        this.weapon = weapon;
        this.critical = critical;
    }

    @Override
    public int effect() {
        return effect.effect().id();
    }

    @Override
    public int min() {
        return applyCriticalBonus(effect.min());
    }

    @Override
    public int max() {
        return applyCriticalBonus(effect.max());
    }

    @Override
    public int special() {
        return effect.extra();
    }

    @Override
    public int duration() {
        return 0;
    }

    @Override
    public int probability() {
        return 0;
    }

    @Override
    public String text() {
        return null;
    }

    @Override
    public SpellEffectArea area() {
        return weapon.effectArea();
    }

    @Override
    public int target() {
        return 0;
    }

    private int applyCriticalBonus(int base) {
        if (critical) {
            return base + weapon.info().criticalBonus();
        }

        return base;
    }
}
