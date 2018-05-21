package fr.quatrevieux.araknemu.game.fight.event;

import fr.quatrevieux.araknemu.game.fight.Fight;

/**
 * The fight is successfully created
 */
final public class FightCreated {
    final private Fight fight;

    public FightCreated(Fight fight) {
        this.fight = fight;
    }

    public Fight fight() {
        return fight;
    }
}
