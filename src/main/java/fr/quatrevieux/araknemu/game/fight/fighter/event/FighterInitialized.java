package fr.quatrevieux.araknemu.game.fight.fighter.event;

import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;

/**
 * Fighter is initialized, and ready for fight
 */
final public class FighterInitialized {
    final private Fighter fighter;

    public FighterInitialized(Fighter fighter) {
        this.fighter = fighter;
    }

    public Fighter fighter() {
        return fighter;
    }
}
