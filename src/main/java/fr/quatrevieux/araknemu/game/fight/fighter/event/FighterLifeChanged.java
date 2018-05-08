package fr.quatrevieux.araknemu.game.fight.fighter.event;

import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;

/**
 * Fighter life is changed
 */
final public class FighterLifeChanged {
    final private Fighter fighter;
    final private Fighter caster;
    final private int value;

    public FighterLifeChanged(Fighter fighter, Fighter caster, int value) {
        this.fighter = fighter;
        this.caster = caster;
        this.value = value;
    }

    public Fighter fighter() {
        return fighter;
    }

    public Fighter caster() {
        return caster;
    }

    public int value() {
        return value;
    }
}
