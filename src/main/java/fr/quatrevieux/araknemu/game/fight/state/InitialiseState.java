package fr.quatrevieux.araknemu.game.fight.state;

import fr.quatrevieux.araknemu.game.fight.Fight;

/**
 * Initialisation of the fight
 */
final public class InitialiseState implements FightState {
    @Override
    public void start(Fight fight) {
        fight.nextState();
    }

    @Override
    public int id() {
        return 1;
    }
}
