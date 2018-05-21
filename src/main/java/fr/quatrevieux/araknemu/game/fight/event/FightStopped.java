package fr.quatrevieux.araknemu.game.fight.event;

import fr.quatrevieux.araknemu.game.fight.Fight;

/**
 * The fight is stopped
 */
final public class FightStopped {
    final private Fight fight;

    public FightStopped(Fight fight) {
        this.fight = fight;
    }

    public Fight fight() {
        return fight;
    }
}
