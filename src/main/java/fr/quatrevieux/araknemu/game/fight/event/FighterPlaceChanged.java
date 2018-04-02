package fr.quatrevieux.araknemu.game.fight.event;

import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;

/**
 * Trigger when an fighter place is changed
 */
final public class FighterPlaceChanged {
    final private Fighter fighter;

    public FighterPlaceChanged(Fighter fighter) {
        this.fighter = fighter;
    }

    public Fighter fighter() {
        return fighter;
    }
}
