package fr.quatrevieux.araknemu.game.fight.event;

import fr.quatrevieux.araknemu.game.fight.Fight;

/**
 * The fight is cancelled
 */
final public class FightCancelled {
    final private Fight fight;

    public FightCancelled(Fight fight) {
        this.fight = fight;
    }

    public Fight fight() {
        return fight;
    }
}
