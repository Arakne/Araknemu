package fr.quatrevieux.araknemu.game.fight.fighter.event;

import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;

/**
 * A fighter is dead
 */
final public class FighterDie {
    final private Fighter fighter;
    final private Fighter caster;

    public FighterDie(Fighter fighter, Fighter caster) {
        this.fighter = fighter;
        this.caster = caster;
    }

    public Fighter fighter() {
        return fighter;
    }

    public Fighter caster() {
        return caster;
    }
}
