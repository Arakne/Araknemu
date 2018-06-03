package fr.quatrevieux.araknemu.game.fight.event;

import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;

/**
 * A new fighter is added to the fight
 */
final public class FighterAdded {
    final private Fighter fighter;

    public FighterAdded(Fighter fighter) {
        this.fighter = fighter;
    }

    public Fighter fighter() {
        return fighter;
    }
}
