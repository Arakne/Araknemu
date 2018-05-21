package fr.quatrevieux.araknemu.game.fight.event;

import fr.quatrevieux.araknemu.game.fight.Fight;

/**
 * The fight is started
 */
final public class FightStarted {
    final private Fight fight;

    public FightStarted(Fight fight) {
        this.fight = fight;
    }

    public Fight fight() {
        return fight;
    }
}
