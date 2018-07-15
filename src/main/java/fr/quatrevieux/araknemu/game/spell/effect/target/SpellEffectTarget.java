package fr.quatrevieux.araknemu.game.spell.effect.target;

import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;

/**
 * Implementation of effect target using spell flags
 */
final public class SpellEffectTarget implements EffectTarget {
    final static public SpellEffectTarget DEFAULT = new SpellEffectTarget(0);

    final static public int NOT_TEAM    = 1;
    final static public int NOT_SELF    = 2;
    final static public int NOT_ENEMY   = 4;
    final static public int ONLY_INVOC  = 8;
    final static public int NOT_INVOC   = 16;
    final static public int ONLY_CASTER = 32;

    final private int flags;

    public SpellEffectTarget(int flags) {
        this.flags = flags;
    }

    @Override
    public boolean onlyCaster() {
        return check(ONLY_CASTER);
    }

    @Override
    public boolean test(Fighter caster, Fighter fighter) {
        if (check(NOT_TEAM) && caster.team().equals(fighter.team())) {
            return false;
        }

        if (check(NOT_SELF) && caster.equals(fighter)) {
            return false;
        }

        if (check(NOT_ENEMY) && !caster.team().equals(fighter.team())) {
            return false;
        }

        if (check(ONLY_INVOC)) {
            return false;
        }

        return true;
    }

    private boolean check(int flag) {
        return (flags & flag) == flag;
    }

    @Override
    public boolean equals(Object obj) {
        return getClass().equals(obj.getClass()) && flags == ((SpellEffectTarget) obj).flags;
    }
}
