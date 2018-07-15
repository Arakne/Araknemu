package fr.quatrevieux.araknemu.game.spell.effect.target;

import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;

/**
 * Handle effect targets
 */
public interface EffectTarget {
    /**
     * Check if the effect only targets the caster
     */
    public boolean onlyCaster();

    /**
     * Check if the fighter is a valid target
     *
     * @param caster The action caster
     * @param fighter Fighter to test
     *
     * @return true if the fighter is a valid target, or false
     */
    public boolean test(Fighter caster, Fighter fighter);
}
