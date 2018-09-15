package fr.quatrevieux.araknemu.game.fight.fighter.event;

import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;

/**
 * The state has changed
 */
final public class FighterStateChanged {
    public enum Type {
        ADD,
        REMOVE,
        UPDATE
    }

    final private Fighter fighter;
    final private int state;
    final private Type type;

    public FighterStateChanged(Fighter fighter, int state, Type type) {
        this.fighter = fighter;
        this.state = state;
        this.type = type;
    }

    public Fighter fighter() {
        return fighter;
    }

    public int state() {
        return state;
    }

    public Type type() {
        return type;
    }
}
