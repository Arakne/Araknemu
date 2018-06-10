package fr.quatrevieux.araknemu.game.fight.event;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;

/**
 * A fighter is removed from the fight
 */
final public class FighterRemoved {
    final private Fighter fighter;
    final private Fight fight;

    public FighterRemoved(Fighter fighter, Fight fight) {
        this.fighter = fighter;
        this.fight = fight;
    }

    public Fighter fighter() {
        return fighter;
    }

    public Fight fight() {
        return fight;
    }
}
