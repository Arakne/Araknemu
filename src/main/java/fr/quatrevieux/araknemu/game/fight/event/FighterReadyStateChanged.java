package fr.quatrevieux.araknemu.game.fight.event;

import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;

/**
 * Ready state of the fighter is changed
 */
final public class FighterReadyStateChanged {
    final private Fighter fighter;

    public FighterReadyStateChanged(Fighter fighter) {
        this.fighter = fighter;
    }

    public Fighter fighter() {
        return fighter;
    }

    public boolean ready() {
        return fighter.ready();
    }
}
