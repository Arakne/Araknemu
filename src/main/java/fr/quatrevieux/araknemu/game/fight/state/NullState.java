package fr.quatrevieux.araknemu.game.fight.state;

import fr.quatrevieux.araknemu.game.fight.Fight;

/**
 * Fight state before initialisation
 */
final public class NullState implements FightState {
    @Override
    public void start(Fight fight) {}

    @Override
    public int id() {
        return 0;
    }
}
