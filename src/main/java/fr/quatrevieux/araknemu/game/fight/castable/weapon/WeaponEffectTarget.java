package fr.quatrevieux.araknemu.game.fight.castable.weapon;

import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.spell.effect.target.EffectTarget;

/**
 * Effect target for weapon
 */
final public class WeaponEffectTarget implements EffectTarget {
    final static public EffectTarget INSTANCE = new WeaponEffectTarget();

    @Override
    public boolean onlyCaster() {
        return false;
    }

    @Override
    public boolean test(Fighter caster, Fighter fighter) {
        return !caster.equals(fighter);
    }
}
